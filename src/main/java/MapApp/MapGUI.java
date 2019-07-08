package MapApp;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  
import java.io.File;
import java.util.Set;
import java.util.Stack;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class MapGUI extends Application {
	
	/*Instance variables*/
	
	private BorderPane pane;
	private MapGraph map;
	private Vertex startpt;
	private Vertex endpt;
	private Set<Vertex> vertices;
	private int clickcounter = 0;
	private Pane nodes;
	private Scene scene;
	private File text;
	private File image;
	

	/*Sets up the starting screen
	 * of the GUI
	 */
	
	@Override 
	public void start(Stage stage) {
		pane = new BorderPane();
		CheckBox events = new CheckBox("Random Events");
		events.setIndeterminate(false);
		Button start = new Button("Start");
		start.setOnAction(new EventHandler<ActionEvent>(){
			@Override 
			public void handle(ActionEvent event) {
				/* If the random events option has not been selected,
				 * finds and displays path found using the A* algorithm
				 */
				if (!events.isSelected() && map != null && startpt != null && endpt != null) {
					PathAlgorithms pathfinders = new PathAlgorithms(startpt, endpt, map);
					Stack<Vertex> path = pathfinders.AStar();
					updateMap(path);
					stage.setScene(scene);
					stage.show();
				}
				/*If the random events option has been selected, 
				 * finds and displays a path found using a genetic algorithm
				 */
				else if (events.isSelected() && map != null && startpt != null && endpt != null){
					PathAlgorithms pathfinders = new PathAlgorithms(startpt, endpt, map);
					Stack<Vertex> path = pathfinders.geneticAlgorithm();	
					updateMap(path);
					stage.setScene(scene);
					stage.show();
				}
			}
		});
		Button clear = new Button("Clear");
		clear.setOnAction(new EventHandler<ActionEvent>(){
			/*Clears the map*/
			@Override
			public void handle(ActionEvent event) {
				if (image != null && text != null) {
					loadMap(image, text);
				}
				startpt = endpt = null;
				stage.setScene(scene);
				stage.show();
			}
		});
		Button mapchooser = new Button("Load a Map");
		mapchooser.setOnAction(new EventHandler<ActionEvent>() {
			/*Accepts folder input from the user containing
			 * a jpg image of the map as well as a pre-formatted 
			 * txt file with information on the map
			 */
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser select = new DirectoryChooser();
				select.setTitle("Select a Map (a folder with a formatted .txt and .jpg file");
				File selection = select.showDialog(stage);
				if (selection != null) {
					process(selection);
					stage.sizeToScene();
					stage.setScene(scene);
					stage.show();
				}
			}
		});
		HBox top = new HBox();
		Region region1 = new Region();
		HBox.setHgrow(region1,  Priority.ALWAYS);
		Region region2 = new Region();
		HBox.setHgrow(region2,  Priority.ALWAYS);
		Region region3 = new Region();
		HBox.setHgrow(region3,  Priority.ALWAYS);
		Region region4 = new Region();
		top.getChildren().addAll(region1, start, region2, clear, region3, events, region4);
		top.setAlignment(Pos.CENTER);
		BorderPane.setAlignment(top, Pos.CENTER);
		pane.setTop(top);
		pane.setBottom(mapchooser);
		BorderPane.setAlignment(mapchooser, Pos.CENTER);
		scene = new Scene(pane);
		stage.setScene(scene);
		stage.setHeight(100);
		stage.setWidth(300);
		stage.show();
	}
	
	/*Extracts the image and text
	 * from the user's file input
	 */
	public void process(File input) {
		File[] files = input.listFiles();
		text = null;
		image = null;
		for (File file: files) {
			if (file.getName().endsWith(".txt")) {
				text = file;
			}
			else if (file.getName().endsWith(".jpg")) {
				image = file;
			}
		}
		if (image != null && text != null) {
			loadMap(image, text);
		} else {
			System.out.println("Improper file input");
		}
	}
	
	/*Creates a new object of type
	 * MapGraph from the user's text file
	 * and uploads the user's map image
	 * onto the screen, displaying a Site for
	 * each point on the map
	 */
	
	public void loadMap(File img, File txt) {
		map = new MapGraph(txt);
		vertices = map.getVertices();
		StackPane overlay = new StackPane();
		nodes = new Pane();
		for (Vertex v: vertices) {
			new Site(v.getX(), v.getY(), v.getName(), v);
		}
		Image picture = new Image(img.toURI().toString());
		ImageView view = new ImageView(picture);
		view.setPreserveRatio(true);
		overlay.getChildren().addAll(view, nodes);
		pane.setCenter(overlay);
	}
	
	public void updateMap(Stack<Vertex> path) {
		Vertex first = path.pop();
		Vertex second = first;
		while (!second.equals(endpt)) {
			second = path.pop();
			Line line = new Line(first.getX(), first.getY(), second.getX(), second.getY());
			first = second;
			nodes.getChildren().add(line);
		}
	}
	
	/*A clickable green circle superimposed
	 * upon the image of the map that
	 * contains coordinates corresponding to its
	 * position as well as its name. When clicked, will be
	 * painted yellow and recorded as either the
	 * start or endpoint.
	 */
	private class Site {
		private double x;
		private double y;
		private String name;
		private Vertex v;
		
		public Site(double x, double y, String name, Vertex v) {
			this.x = x;
			this.y = y;
			this.name = name;
			this.v = v;
			Circle circle = new Circle(x, y, 4);
			circle.setFill(Color.GREEN);
			circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					Paint color = circle.getFill();
					if (color.equals(Color.YELLOW)) {
						circle.setFill(Color.GREEN);
						if (clickcounter == 1) {
							clickcounter--;
							startpt = null;
						}
						else if (clickcounter == 2) {
							clickcounter--;
							endpt = null;
						}
					}
					else if (color.equals(Color.GREEN)) {
						if (clickcounter == 0) {
							circle.setFill(Color.YELLOW);
							clickcounter++;
							startpt = v;
						}
						else if (clickcounter == 1) {
							circle.setFill(Color.YELLOW);
							clickcounter++;
							endpt = v;
						}
					}
				}
			});
			nodes.getChildren().add(circle);
		}	
	}
	
	public static void main(String[] args) {
		launch(args);
	}	
}