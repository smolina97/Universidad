package nightmare2;

import java.io.IOException;

import org.jdesktop.application.FrameView;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ViewFX extends FrameView
{
	private App app;
	private JFXPanel panel;
	
	public ViewFX(App app) {
		super(app);
		this.app = app;

		initComponents();
	}

	private void initComponents() {
		panel = new JFXPanel();
		try
		{
			
			Parent parent = FXMLLoader.load(this.getClass().getResource("fxml/nightmare.fxml"));
			Scene scene = new Scene(parent, 260, 450);
			panel.setScene(scene);
			app.getMainFrame().setMinimumSize(panel.getMinimumSize());
			this.getFrame().add(panel);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
