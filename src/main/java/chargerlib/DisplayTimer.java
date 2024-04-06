/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chargerlib;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * An hour-minute-second timer associated with a particular JLabel display.
 * Works as either a count-up timer from zero or a count-down timer, where 
 * the display switches to yellow when it reaches 2 minutes and an alternating red-color after it reaches zero.
 * Resolution of the timer is assumed to be no better than one second.
 * @author Harry S. Delugach (delugach@uah.edu)
 * @see javax.swing.Timer
 */
public class DisplayTimer {

    JLabel displayLabel = null;
    boolean countdown = false;
    long durationTimeMilliseconds = 0;
    long zeroTimeInMilliseconds = 0;
    boolean flashRedAfterExpiration = false;
    boolean showYellowAfterWarning = false;
        
    long warningSeconds = 120;
    
    Toolkit toolkit = Toolkit.getDefaultToolkit();

    /**
     * The value to display ... if negative, then will be shown as red-based
     * display.
     */
    long displayedTimeSeconds = 0;
    Date startTime = null;
    Timer myTimer = new Timer( 0, null );
    Color foreground = null;


    /** Associates this timer with the given label */
    public DisplayTimer( JLabel displayField ) {
        this( displayField, false, 0 );
    }

    /**
     *
     * @param displayLabel The field to use for the display
     * @param countdown Whether we are counting down from the start value or counting up.
     * @param startValue The starting value in seconds. If zero, then startTimer starts from the current time. 
     * Otherwise startTimer starts with the current time plus the start value.
     */
    public DisplayTimer( JLabel displayLabel, boolean countdown, int durationSeconds ) {
        this.displayLabel = displayLabel;
        this.countdown = countdown;
        this.durationTimeMilliseconds = durationSeconds * 1000;
        
        this.foreground = displayLabel.getForeground();
    }

    public void setDisplayLabel( JLabel displayLabel ) {
        this.displayLabel = displayLabel;
        this.foreground = displayLabel.getForeground();
    }

    /** Set the number of seconds to start the countdown.
     * 
     * @param countdownSeconds Countdown will start here. Note there are no restrictions on the value;
     * if given a negative number, then it will countdown from that value,
     * flashing red as it goes.
     */
    public void setCountdownSeconds( int countdownSeconds  ) {
        this.durationTimeMilliseconds = countdownSeconds * 1000;
    }
    
    public void setText( String text ) {
        if ( displayLabel != null ) 
            displayLabel.setText( text );
    }

    public boolean isFlashRedAfterExpiration() {
        return flashRedAfterExpiration;
    }

    public void setFlashRedAfterExpiration( boolean flashRedAfterExpiration ) {
        this.flashRedAfterExpiration = flashRedAfterExpiration;
    }

    public boolean isShowYellowAfterWarning() {
        return showYellowAfterWarning;
    }

    public void setShowYellowAfterWarning( boolean showYellowAfterWarning ) {
        this.showYellowAfterWarning = showYellowAfterWarning;
    }

    public long getWarningSeconds() {
        return warningSeconds;
    }

    public void setWarningSeconds( long warningSeconds ) {
        this.warningSeconds = warningSeconds;
    }
    
    
    
    public void stopTimer() {
        if ( myTimer != null && myTimer.isRunning() )
            myTimer.stop();
        displayLabel.setForeground( foreground );
        displayLabel.setText(  hms( 0 ));
    }
    
    public boolean isRunning() {
        return myTimer.isRunning();
    }
    
    /** Reset the timer to the countdown seconds and start over.
     * Equivalent to calling startTimer with restart true.
     * @param countdownSeconds 
     */
    public void restartTimer( int countdownSeconds ) {
        startTimer( countdownSeconds, true );
    }
    
    /**
     * Begin the countdown for the timer.
     * @param countdownSeconds the number of seconds to start with
     * @param restart true if we want to reset a running timer to the countdown seconds; false if we continue
     * counting down (i.e., ignore this call if the timer is already running)
     */
    public void startTimer( int countdownSeconds, boolean restart ) {
        if ( isRunning() && ! restart )
            return;
        this.durationTimeMilliseconds = countdownSeconds * 1000;
        startTimer();
    }
    
    /**
     * Create a one-second timer for the elapsed time display, and start the
     * timer.
     *
     */
    public void startTimer() {
        stopTimer();
        zeroTimeInMilliseconds = new Date().getTime();
        if ( countdown ) {
            displayLabel.setText( DisplayTimer.hms( (int)( this.durationTimeMilliseconds / 1000 ) ) );
        } else {
            displayLabel.setText( DisplayTimer.hms( 0 ) );
        }
        myTimer = new Timer( 1000, new ActionListener() {
            boolean displayRed = true;
            boolean displayYellow = true;
            boolean warningBeeped = false;
            boolean expireBeeped = false;

            public void actionPerformed( ActionEvent evt ) {
                Date now = new Date();
                long displayedTimeInSeconds;
                if ( !countdown ) {
                    displayedTimeInSeconds = ( now.getTime() - zeroTimeInMilliseconds ) / 1000;
                } else {
                    displayedTimeInSeconds = ( durationTimeMilliseconds - ( now.getTime() - zeroTimeInMilliseconds ) ) / 1000;
                    if ( flashRedAfterExpiration && displayedTimeInSeconds <= 0 ) {
                        if ( ! expireBeeped ) {
                            toolkit.beep();
                            toolkit.beep();
                            expireBeeped = true;
                        }
                        displayLabel.setForeground( displayRed ? Color.red : foreground );
                        displayLabel.setForeground( Color.red );
                        displayRed = !displayRed;
                    } else if ( showYellowAfterWarning && displayedTimeInSeconds <= warningSeconds ) {
                        if ( ! warningBeeped ) {
                            toolkit.beep();
                            toolkit.beep();
                            warningBeeped = true;
                        }
                        displayLabel.setForeground( Color.yellow );
                    }
                }
                displayLabel.setText( DisplayTimer.hms( (int)displayedTimeInSeconds ) );
            }
        } );
        myTimer.start();
    }


    /**
     * Converts number of seconds into a nice 00:00:00 string.
     */
    public static String hms( int intervalSeconds ) {
        boolean negative = false;
        if ( intervalSeconds < 0 ) {
            negative = true;
            intervalSeconds = Math.abs( intervalSeconds);
        }
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits( 0 );
        format.setMinimumIntegerDigits( 2 );
        format.setMaximumIntegerDigits( 2 );
        int hrs = intervalSeconds / ( 60 * 60 );
        int mins = ( intervalSeconds - ( hrs * 60 * 60 ) ) / 60;
        int secs = intervalSeconds - mins * 60 - hrs * 60 * 60;
        return (negative ? "- ":"") + format.format( hrs ) + ":" + format.format( mins ) + ":" + format.format( secs );
    }

}
