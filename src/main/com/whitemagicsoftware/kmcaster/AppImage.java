package com.whitemagicsoftware.kmcaster;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

import java.net.URL;

import static java.lang.String.format;

/**
 * Responsible for loading vector graphics representations of application
 * images. The images provide an on-screen interface that indicate to the user
 * what key or mouse events are being triggered.
 */
public class AppImage {
  private final static String IMAGES = "/images";
  private final static String IMAGES_KEY = IMAGES + "/key";
  private final static String IMAGES_MOUSE = IMAGES + "/mouse";

  public static final AppImage MOUSE_LEFT = mouseImage( "0" );
  public static final AppImage MOUSE_RIGHT = mouseImage( "1" );
  public static final AppImage MOUSE_CHORD = mouseImage( "2" );
  public static final AppImage MOUSE_WHEEL = mouseImage( "3" );
  public static final AppImage MOUSE_ALL = mouseImage( "all" );
  public static final AppImage KEY_UP_SHIFT = keyUpImage( "long" );
  public static final AppImage KEY_UP_ALT = keyUpImage( "medium" );
  public static final AppImage KEY_UP_CTRL = keyUpImage( "medium" );
  public static final AppImage KEY_UP_REGULAR = keyUpImage( "short" );
  public static final AppImage KEY_DN_SHIFT = keyDnImage( "long" );
  public static final AppImage KEY_DN_ALT = keyDnImage( "medium" );
  public static final AppImage KEY_DN_CTRL = keyDnImage( "medium" );
  public static final AppImage KEY_DN_REGULAR = keyDnImage( "short" );

  private static AppImage mouseImage( final String prefix ) {
    return createImage( format( "%s/%s", IMAGES_MOUSE, prefix ) );
  }

  private static AppImage keyImage( final String state, final String prefix ) {
    return createImage( format( "%s/%s/%s", IMAGES_KEY, state, prefix ) );
  }

  private static AppImage keyUpImage( final String prefix ) {
    return keyImage( "up", prefix );
  }

  private static AppImage keyDnImage( final String prefix ) {
    return keyImage( "dn", prefix );
  }

  private static AppImage createImage( final String path ) {
    return new AppImage( format( "%s.svg", path ) );
  }

  private final SVGUniverse mRenderer = new SVGUniverse();
  private final String mPath;

  /**
   * Constructs an enumerated type that represents the different types of
   * images shown when keyboard and mouse events are triggered.
   *
   * @param path File name, including directory, to load.
   */
  private AppImage( final String path ) {
    mPath = path;
  }

  public SVGDiagram getImage() {
    final var url = getResourceUrl();
    return mRenderer.getDiagram( mRenderer.loadSVG( url ) );
  }

  private URL getResourceUrl() {
    return AppImage.class.getResource( getPath() );
  }

  private String getPath() {
    return mPath;
  }
}
