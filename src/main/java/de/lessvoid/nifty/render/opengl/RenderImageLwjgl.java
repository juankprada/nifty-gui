package de.lessvoid.nifty.render.opengl;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import de.lessvoid.nifty.render.RenderImage;
import de.lessvoid.nifty.render.RenderImageSubImageMode;
import de.lessvoid.nifty.tools.Color;

/**
 * Lwjgl/Slick implementation for the RenderImage interface.
 * @author void
 */
public class RenderImageLwjgl implements RenderImage {

  /**
   * The slick image.
   */
  private org.newdawn.slick.Image image;

  /**
   * sub image type to use.
   */
  private RenderImageSubImageMode subImageMode;

  /**
   * resize helper for the ResizeHint scale type.
   */
  private ResizeHelper resizeHelper;

  /**
   * sub image source x position.
   */
  private int subImageX;

  /**
   * sub image source y position.
   */
  private int subImageY;

  /**
   * sub image source width.
   */
  private int subImageW;

  /**
   * sub image source height.
   */
  private int subImageH;

  /**
   * Create a new RenderImage.
   * @param name the name of the resource in the file system
   * @param filter use linear filter (true) or nearest filter (false)
   */
  public RenderImageLwjgl(final String name, final boolean filter) {
    this.subImageMode = RenderImageSubImageMode.NORMAL();

    try {
      this.image = new org.newdawn.slick.Image(name, false, filter ? Image.FILTER_LINEAR : Image.FILTER_NEAREST);
    } catch (SlickException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get height of image.
   * @return height
   */
  public int getHeight() {
    return image.getHeight();
  }

  /**
   * Get width of image.
   * @return width
   */
  public int getWidth() {
    return image.getWidth();
  }

  /**
   * Render the image using the given Box to specify the render attributes.
   * @param x x
   * @param y y
   * @param width width
   * @param height height
   * @param color color
   * @param scale scale
   */
  public void render(
      final int x, final int y, final int width, final int height, final Color color, final float scale) {
    if (subImageMode.equals(RenderImageSubImageMode.SCALE())) {
      renderScale(x, y, width, height, subImageX, subImageY, subImageW, subImageH, color);
    } else if (subImageMode.equals(RenderImageSubImageMode.RESIZE())) {
      resizeHelper.performRender(x, y, width, height, color);
    } else {
      renderNormal(x, y, width, height, color, scale);
    }
  }

  /**
   * Actual perform the render.
   * @param x x
   * @param y y
   * @param width w
   * @param height h
   * @param color color
   * @param scale scale
   */
  private void renderNormal(
      final int x, final int y, final int width, final int height, final Color color, final float scale) {
    RenderTools.beginRender();
    GL11.glTranslatef(x + width / 2, y + height / 2, 0.0f);
    GL11.glScalef(scale, scale, 1.0f);
    GL11.glTranslatef(-(x + width / 2), -(y + height / 2), 0.0f);
    image.bind();
    image.draw(x, y, width, height, convertToSlickColor(color));
    RenderTools.endRender();
  }

  /**
   * Render sub image.
   * @param x x
   * @param y y
   * @param w w
   * @param h h
   * @param srcX x
   * @param srcY y
   * @param srcW w
   * @param srcH h
   * @param color color
   */
  public void renderScale(
      final int x,
      final int y,
      final int w,
      final int h,
      final int srcX,
      final int srcY,
      final int srcW,
      final int srcH,
      final Color color) {
    RenderTools.beginRender();
    image.bind();
    image.draw(x, y, x + w, y + h, srcX, srcY, srcX + srcW, srcY + srcH, convertToSlickColor(color));
    RenderTools.endRender();
  }

  /**
   * Set a new sub image active state.
   * @param newSubImageMode new type
   */
  public void setSubImageMode(final RenderImageSubImageMode newSubImageMode) {
    this.subImageMode = newSubImageMode;
  }

  /**
   * Set the resize hint.
   * @param resizeHint string representing the resize hint
   */
  public void setResizeHint(final String resizeHint) {
    resizeHelper = new ResizeHelper(this, resizeHint);
  }

  /**
   * Set a new SubImage size.
   * @param newSubImageX sub image x
   * @param newSubImageY sub image y
   * @param newSubImageW sub image width
   * @param newSubImageH sub image height
   */
  public void setSubImage(
      final int newSubImageX,
      final int newSubImageY,
      final int newSubImageW,
      final int newSubImageH) {
    this.subImageX = newSubImageX;
    this.subImageY = newSubImageY;
    this.subImageW = newSubImageW;
    this.subImageH = newSubImageH;
  }

  /**
   * Convert a Nifty color to a Slick color.
   * @param color nifty color
   * @return slick color
   */
  private org.newdawn.slick.Color convertToSlickColor(final Color color) {
    if (color != null) {
      return new org.newdawn.slick.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    } else {
      return null;
    }
  }

}
