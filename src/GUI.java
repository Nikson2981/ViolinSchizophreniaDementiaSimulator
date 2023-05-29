import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Nikki
 * @since 2023.28.05
 */
public class GUI extends JPanel implements ActionListener {

    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    public int doOnceKeycode = NativeKeyEvent.VC_F5, toggleKeycode = NativeKeyEvent.VC_F6, killswitchCode = NativeKeyEvent.VC_F7;
    private boolean waitingForHotkey = false, alreadyRunning = false, threadRunning = false;
    private JTextField hotkeyField;
    private final Robot input;

    public GUI() throws AWTException {
        input = new Robot();
        JLabel title = new JLabel("ViolinSchizophreniaDementiaSimulator Controls");
        JLabel info1 = new JLabel("Toggle: F6");
        JLabel info2 = new JLabel("Exit: F7");
        JLabel info3 = new JLabel("Do once: F8");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 21)));

        this.add(info1);
        this.add(info2);
        this.add(info3);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            JOptionPane.showMessageDialog(this,
                    "Error when setting hotkey",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
    }

    public class GlobalKeyListener implements NativeKeyListener {
        public void nativeKeyPressed(NativeKeyEvent keyPress) {

            if (keyPress.getKeyCode() == doOnceKeycode) {
                if (threadRunning | alreadyRunning) return;
                alreadyRunning = true;
                resetSong();
            }

            if (keyPress.getKeyCode() == toggleKeycode) {
                if (threadRunning) {
                    threadRunning = false;
                    return;
                }
                threadRunning = true;
                new Thread(() -> {
                    try {
                        while (threadRunning) {
                            resetSong();
                            Thread.sleep(1495);
                        }
                    } catch (Exception ignored) {
                    }
                }).start();
            }

            if (keyPress.getKeyCode() == killswitchCode) {
                System.exit(0);
            }

            // If we're waiting for a hotkey to be pressed, make sure we can set it all up
            if (waitingForHotkey) {
                doOnceKeycode = keyPress.getKeyCode();
                hotkeyField.setText(NativeKeyEvent.getKeyText(doOnceKeycode));

                //"Execute (" + NativeKeyEvent.getKeyText(hotkeyCode) + ")"

                waitingForHotkey = false;
            }
        }

        public void nativeKeyReleased(NativeKeyEvent keyPress) { // unneeded
        }

        public void nativeKeyTyped(NativeKeyEvent keyPress) { // no worky must be there
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: this
    }

    private void resetSong() {
        try {
            input.mousePress(InputEvent.BUTTON3_DOWN_MASK); // button3 == mouse2 fsr
            input.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            input.delay(10);

            input.keyPress(KeyEvent.VK_Q);
            input.keyRelease(KeyEvent.VK_Q);
            input.delay(20); // wait a bit longer because hf likes to move the mouse back to the center of the screen for no reason sometimes (so we just move it after that happens)

            input.mouseMove(SCREEN_SIZE.width / 2 + SCREEN_SIZE.width / 16, SCREEN_SIZE.height - SCREEN_SIZE.height / 6 - 5); // brute force estimate of where the button for Tempo Di Borea will be... only tested on 1440p
            input.delay(10);
            input.mousePress(InputEvent.BUTTON1_DOWN_MASK); // click once we're there
            input.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        alreadyRunning = false;
    }

    public static void main(String[] args) throws AWTException {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame window = new JFrame("ViolinSchizophreniaDementiaSimulator");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GUI gui = new GUI();
        window.getContentPane().add(gui);
        window.pack();
        window.setLocation(300, 300);
        window.setVisible(true);
    }
}
