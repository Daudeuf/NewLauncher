package fr.clem76.view;

import fr.clem76.back.Authentication;
import fr.clem76.back.DataReceiver;
import fr.clem76.back.Game;
import fr.clem76.back.ServerData;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static fr.clem76.Main.LAUNCHER_LABEL;

public class MainFrame extends JFrame implements ActionListener {

    private static MainFrame instance;

    public static final Color BACKGROUND = Color.decode("#1A1A1A");
    public static final Color PRIMARY = Color.decode("#F0F0F0");
    public static final Color ACCENT_1 = Color.decode("#004D61");
    public static final Color ACCENT_2 = Color.decode("#822659");
    public static final Color HOVER = Color.decode("#3E5641");

    private BorderLayout layout = new BorderLayout();
    private JButton btnDisconnect = new JButton("Déconnexion");
    private JButton btnPlay = new JButton("Jouer");
    private JButton btnOptions = new JButton("Options");
    private JProgressBar progressBar = new JProgressBar();

    private final Image imgBackground;
    private final Image imgTitle;
    private Image imgPlayer;

    private JPanel panel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imgBackground == null) return;

            int pw = getWidth(), ph = getHeight();
            int iw = imgBackground.getWidth(this), ih = imgBackground.getHeight(this);
            if (iw <= 0 || ih <= 0) return;

            double scale = Math.max((double) pw / iw, (double) ph / ih);
            int w = (int) (iw * scale), h = (int) (ih * scale);
            int x = (pw - w) / 2, y = (ph - h) / 2;

            g.drawImage(imgBackground, x, y, w, h, this);

            if (imgPlayer != null) g.drawImage(imgPlayer, pw-150, ph/2-100, this);
            if (imgTitle != null) g.drawImage(imgTitle, pw/2-250, 50, this);
        }
    };

    public MainFrame() {
        this.setTitle(LAUNCHER_LABEL);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 606);
        this.setLocationRelativeTo(null);

        this.add(panel);
        panel.setLayout(layout);
        panel.setBackground(BACKGROUND);

        // SOUTH
        JPanel panelSouth = new JPanel(new GridLayout(2, 1, 5, 5));
        panelSouth.setOpaque(false);
        panel.add(panelSouth, BorderLayout.SOUTH);

        JPanel panelSouthUp = new JPanel();
        panelSouthUp.add(btnPlay);
        panelSouthUp.setOpaque(false);

        JPanel panelSouthDown = new JPanel();
        panelSouthDown.add(progressBar);
        panelSouthDown.setOpaque(false);

        panelSouth.add(panelSouthUp);
        panelSouth.add(panelSouthDown);
        progressBar.setPreferredSize(new Dimension(1100, 15));
        progressBar.setVisible(false);
        progressBar.setMaximum(1000);

        // LEFT
        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        panelLeft.setOpaque(false);
        panelLeft.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(panelLeft, BorderLayout.WEST);

        btnOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDisconnect.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelLeft.add(Box.createVerticalGlue());
        panelLeft.add(btnOptions);
        panelLeft.add(Box.createRigidArea(new Dimension(0, 10)));
        panelLeft.add(btnDisconnect);
        panelLeft.add(Box.createVerticalGlue());


        // COLORIZE
        btnPlay.setBackground(ACCENT_2);
        btnOptions.setBackground(ACCENT_1);
        btnDisconnect.setBackground(ACCENT_1);
        btnPlay.setForeground(PRIMARY);
        btnOptions.setForeground(PRIMARY);
        btnDisconnect.setForeground(PRIMARY);

        progressBar.setForeground(PRIMARY);
        progressBar.setBackground(BACKGROUND);

        btnPlay.setFocusPainted(false);
        btnOptions.setFocusPainted(false);
        btnDisconnect.setFocusPainted(false);

        btnPlay.setPreferredSize(new Dimension(150, 30));

        btnOptions.setFocusable(false);
        btnDisconnect.setFocusable(false);

        btnPlay.addActionListener(this);
        btnDisconnect.addActionListener(this);
        btnOptions.addActionListener(this);

        this.refreshPlayerImage();

        final String[] lst = new String[] {
                //"mariana-salimena-birch-forest-artstation.png",
                "mariana-salimena-swamp-artstation.png",
                "mariana-salimena-swamp-b-artstation.png"
        };

        this.imgBackground = getToolkit().getImage ( getClass().getResource(String.format("/art/%s", lst[(int) (Math.random()*lst.length)])) );
        this.imgTitle = getToolkit().getImage ( getClass().getResource("/art/diamoria_title.png") );

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));
        this.setIconImage(icon.getImage());

        this.setVisible(true);
    }

    public void refreshPlayerImage() {
        try
        {
            String player = Authentication.getInstance().getAuthInfos().getUsername();
            URI url = new URI(String.format("https://mc-heads.net/player/%s/100.png", player));

            this.imgPlayer = new ImageIcon(url.toURL()).getImage();
        }
        catch (URISyntaxException | MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void launch() {
        if (instance == null) instance = new MainFrame();
        else instance.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.btnDisconnect) {
            Authentication.getInstance().disconnect();
            Runtime.getRuntime().exit(0);
        }

        if (e.getSource() == this.btnPlay) {
            try {
                JSONArray array = DataReceiver.data.getJSONArray("oldIp");
                ArrayList<String> l = new ArrayList<>();
                for (Object o : array) l.add(o.toString());

                ServerData.init(DataReceiver.data.getString("ip"), l);
            } catch (IOException _) {}

            Game.setupAndStart(this);
        }

        if (e.getSource() == this.btnOptions) {

        }
    }

    public JProgressBar getProgressbar() {
        return this.progressBar;
    }

    public void setButtonState(boolean state) {
        this.btnPlay.setEnabled(state);
        this.btnDisconnect.setEnabled(state);
        this.btnOptions.setEnabled(state);
    }
}
