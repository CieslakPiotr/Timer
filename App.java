import javax.swing.*;
import java.awt.*;

public class App {
  static final Color BG = new Color(0x1E1E2E), FG = new Color(0xCDD6F4),
      ACCENT = new Color(0x89B4FA), MUTED = new Color(0x45475A);

  static long remainingMs, endAt;
  static JLabel time;
  static JButton startBtn;
  static JSpinner minutes;
  static Timer tick = new Timer(200, e -> update());

  public static void main(String[] args) {
    assert fmt(1).equals("00:01") && fmt(60_000).equals("01:00"); 
    SwingUtilities.invokeLater(App::build);
  }

  static void build() {
    JFrame f = new JFrame("Timer");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel root = new JPanel(new BorderLayout(0, 20));
    root.setBackground(BG);
    root.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

    time = new JLabel("", SwingConstants.CENTER);
    time.setFont(new Font(Font.MONOSPACED, Font.BOLD, 96));
    time.setForeground(FG);

    minutes = new JSpinner(new SpinnerNumberModel(5, 1, 999, 1));
    minutes.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
    minutes.addChangeListener(e -> { if (!tick.isRunning()) reset(); });
    JLabel minLabel = new JLabel("min");
    minLabel.setForeground(FG);
    minLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));

    startBtn = button("Start", ACCENT, BG);
    startBtn.addActionListener(e -> { if (tick.isRunning()) pause(); else start(); });
    JButton resetBtn = button("Reset", MUTED, FG);
    resetBtn.addActionListener(e -> reset());

    JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    controls.setOpaque(false);
    controls.add(minutes);
    controls.add(minLabel);
    controls.add(startBtn);
    controls.add(resetBtn);

    root.add(time, BorderLayout.CENTER);
    root.add(controls, BorderLayout.SOUTH);
    f.setContentPane(root);
    reset();
    f.pack();
    f.setLocationRelativeTo(null);
    f.setVisible(true);
  }

  static JButton button(String text, Color bg, Color fg) {
    JButton b = new JButton(text);
    b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
    b.setBackground(bg);
    b.setForeground(fg);
    b.setOpaque(true);          
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setPreferredSize(new Dimension(110, 44));
    return b;
  }

  static void start() {
    if (remainingMs <= 0) reset();
    endAt = System.currentTimeMillis() + remainingMs; 
    tick.start();
    startBtn.setText("Pause");
  }

  static void pause() {
    tick.stop();
    remainingMs = endAt - System.currentTimeMillis();
    startBtn.setText("Start");
  }

  static void reset() {
    tick.stop();
    remainingMs = ((Integer) minutes.getValue()) * 60_000L;
    startBtn.setText("Start");
    time.setForeground(FG);
    time.setText(fmt(remainingMs));
  }

  static void update() {
    long left = endAt - System.currentTimeMillis();
    if (left <= 0) {
      tick.stop();
      remainingMs = 0;
      startBtn.setText("Start");
      time.setForeground(ACCENT);
      Toolkit.getDefaultToolkit().beep();
    }
    time.setText(fmt(Math.max(left, 0)));
  }

  static String fmt(long ms) {
    long s = (ms + 999) / 1000; 
    return String.format("%02d:%02d", s / 60, s % 60);
  }
}
