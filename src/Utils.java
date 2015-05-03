import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Utils {

	public static String id = "ddf98bef29f07678c4677cbe639b0768";

	public static void paste(JTextArea textArea) {
		String text = getTextFromClipboard();
		if (text == null) {
			textArea.setText("Error parsing clipboard");
		} else {
			textArea.setText(text);
		}
	}

	public static String getTextFromClipboard() {
		Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				return (String) transferable.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void uploadText(String text, JTextField textField) {
		PastebinPost post = new PastebinPost();
		post.put("api_dev_key", id);
		post.put("api_option", "paste");
		post.put("api_paste_code", text);
		post.put("api_paste_private", "0");
		
		String url = getContents("http://pastebin.com/api/api_post.php", post);
		textField.setText(url);

		StringSelection stringSelection = new StringSelection(url);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	public static String getContents(String link, PastebinPost post) {
		try {
			URL url = new URL(link);

			URLConnection connection = url.openConnection();

			if (post != null) {
				connection.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
				wr.write(post.getPost());
				wr.flush();
				wr.close();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (builder.length() > 0) {
					builder.append('\n');
				}
				builder.append(line);
			}
			reader.close();
			return new String(builder);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed link: " + e);
		} catch (IOException e) {
			throw new RuntimeException("Failed to fetch contents from link: " + e);
		}
	}
}
