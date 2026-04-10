import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;

class DownloadWorker extends Thread {
    private String fileURL;
    private int startByte, endByte;
    private String outputFile;
    private JTextArea logArea;

    public DownloadWorker(String fileURL, int startByte, int endByte, String outputFile, JTextArea logArea) {
        this.fileURL = fileURL;
        this.startByte = startByte;
        this.endByte = endByte;
        this.outputFile = outputFile;
        this.logArea = logArea;
    }

    public void run() {
        try {
            URL url = new URL(fileURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

            InputStream input = conn.getInputStream();
            RandomAccessFile file = new RandomAccessFile(outputFile, "rw");

            file.seek(startByte);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                file.write(buffer, 0, bytesRead);
            }

            file.close();
            input.close();

            logArea.append("Downloaded: " + startByte + "-" + endByte + "\n");

        } catch (Exception e) {
            logArea.append("Error: " + e.getMessage() + "\n");
        }
    }
}

public class MultiThreadDownloader extends JFrame {

    JTextField urlField, fileField, threadField;
    JButton downloadBtn;
    JTextArea logArea;

    public MultiThreadDownloader() {

        setTitle("MultiThread Downloader");
        setSize(550, 400);
        setLocationRelativeTo(null); // center screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== MAIN PANEL =====
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel urlLabel = new JLabel("File URL:");
        urlField = new JTextField();

        JLabel fileLabel = new JLabel("Output File:");
        fileField = new JTextField();

        JLabel threadLabel = new JLabel("Threads:");
        threadField = new JTextField("4");

        formPanel.add(urlLabel);
        formPanel.add(urlField);
        formPanel.add(fileLabel);
        formPanel.add(fileField);
        formPanel.add(threadLabel);
        formPanel.add(threadField);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        downloadBtn = new JButton("Start Download");

        downloadBtn.setBackground(new Color(0, 123, 255));
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.setFont(new Font("Arial", Font.BOLD, 14));
        downloadBtn.setFocusPainted(false);

        buttonPanel.add(downloadBtn);

        // ===== LOG AREA =====
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        // ===== ADD TO MAIN PANEL =====
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);

        // ===== BUTTON ACTION =====
        downloadBtn.addActionListener(e -> startDownload());
    }

    private void startDownload() {
        new Thread(() -> {
            try {
                String fileURL = urlField.getText();
                String outputFile = fileField.getText();
                int numThreads = Integer.parseInt(threadField.getText());

                URL url = new URL(fileURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int fileSize = conn.getContentLength();

                logArea.append("File Size: " + fileSize + "\n");

                if (fileSize <= 0) {
                    logArea.append("Invalid file size\n");
                    return;
                }

                int partSize = fileSize / numThreads;
                DownloadWorker[] workers = new DownloadWorker[numThreads];

                int start = 0;

                for (int i = 0; i < numThreads; i++) {
                    int end = (i == numThreads - 1) ? fileSize - 1 : start + partSize - 1;

                    workers[i] = new DownloadWorker(fileURL, start, end, outputFile, logArea);
                    workers[i].start();

                    logArea.append("Thread " + (i + 1) + ": " + start + "-" + end + "\n");

                    start = end + 1;
                }

                for (int i = 0; i < numThreads; i++) {
                    workers[i].join();
                }

                logArea.append("Download Complete!\n");

            } catch (Exception ex) {
                logArea.append("Error: " + ex.getMessage() + "\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MultiThreadDownloader().setVisible(true);
        });
    }
}