package com.whitemagicsoftware.kmcaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.Color.WHITE;
import static java.awt.Font.BOLD;
import static javax.swing.SwingUtilities.invokeLater;

public class TransparentFrame extends JFrame {
  public static class TransparentLabel extends JLabel {
    public TransparentLabel( final String text ) {
      super( text );
    }

    @Override
    protected void paintComponent( final Graphics g ) {
      final var g2d = (Graphics2D) g.create();
      g2d.clearRect( 0, 0, getWidth(), getHeight() );
      g2d.dispose();

      super.paintComponent( g );
    }
  }

  private final JLabel mLabel = new TransparentLabel( "Begin Listening" );

  public TransparentFrame() { }

  public void init() {
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setLocationRelativeTo( null );
    setUndecorated( true );
    setAlwaysOnTop( true );
    setSize( 600, 300 );
    setFocusTraversalKeysEnabled( false );
    setLayout( new FlowLayout() );
    addMouseListener( new MouseAdapter() {
      @Override
      public void mousePressed( final MouseEvent e ) {
        mLabel.setText( "Pressed" );
      }

      @Override
      public void mouseReleased( final MouseEvent e ) {
        mLabel.setText( "Released" );
      }
    } );

    setBackground( new Color( .1f, .1f, .5f, .2f ) );

    mLabel.setFont( new Font( "defaultFont", BOLD, 30 ) );
    mLabel.setForeground( WHITE );

    add( mLabel );

    setResizable( false );
    setLocationRelativeTo( null );
    setVisible( true );
  }

  public static void main( String[] args ) {
    final var frame = new TransparentFrame();
    invokeLater( frame::init );
  }
}
