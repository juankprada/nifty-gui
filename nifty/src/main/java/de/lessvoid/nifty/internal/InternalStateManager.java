/*
 * Copyright (c) 2015, Nifty GUI Community 
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are 
 * met: 
 * 
 *  * Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.lessvoid.nifty.internal;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

import de.lessvoid.nifty.api.NiftyNodeState;
import de.lessvoid.nifty.api.NiftyStateManager;
import de.lessvoid.nifty.api.NiftyStateSetter;

/**
 * This class manages all the NiftyNodeState for the InternalNiftyNode it's attached to.
 * @author void
 */
public class InternalStateManager implements NiftyStateManager {
  // The states this NiftyNode is currently in. This should always contain at least NiftyNodeState.regular
  private EnumSet<NiftyNodeState> states = EnumSet.of(NiftyNodeState.Regular);

  // Keep for each state a StateData instance that keeps all the actual properties set for this specific state.
  private Map<NiftyNodeState, StateData> stateInfos = new TreeMap<NiftyNodeState, StateData>();

  public InternalStateManager() {
    for (int i=0; i<NiftyNodeState.values().length; i++) {
      this.stateInfos.put(NiftyNodeState.values()[i], new StateData());
    }
  }

  /* (non-Javadoc)
   * @see de.lessvoid.nifty.internal.NiftyStateManager#setValue(V, de.lessvoid.nifty.api.NiftyStateSetter, de.lessvoid.nifty.api.NiftyNodeState)
   */
  @Override
  public <T, V> void setValue(final T target, final V value, final NiftyStateSetter<T, V> setter, final NiftyNodeState ... states) {
    if (targetIsRegularState(states)) {
      stateInfos.get(NiftyNodeState.Regular).set(target, value, setter);
      setter.set(target, value, NiftyNodeState.Regular);
      return;
    }

    for (int i=0; i<states.length; i++) {
      stateInfos.get(states[i]).set(target, value, setter);
      if (states[i] == NiftyNodeState.Regular) {
        setter.set(target, value, NiftyNodeState.Regular);
      }
    }
  }

  public <T> void activateStates(final NiftyNodeState ... newStates) {
    states.clear();
    states.add(NiftyNodeState.Regular);
    states.addAll(Arrays.asList(newStates));
    displayStates();
  }

  public String toString() {
    return "current states: " + states.toString() + ", available: " + stateInfos.toString();
  }

  private <T> void displayStates() {
    for (NiftyNodeState state : states) {
      stateInfos.get(state).apply(state);
    }
  }

  private boolean targetIsRegularState(final NiftyNodeState[] states) {
    if (states == null) {
      return true;
    }
    if (states.length == 0) {
      return true;
    }
    if (states.length > 1) {
      return false;
    }
    return states[0] == NiftyNodeState.Regular;
  }
}