package Controller;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import Model.Car;
import Model.Road;
import Model.TrafficLight;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;

public class Main extends Application {

	public TrafficLight t1;
	public TrafficLight t2;
	public TrafficLight t3;
	public static AnchorPane loader;
	Road r1;
	Road r2;
	Road r3;
	Thread th1, th2, th3;
	Button start, pause, exit;
	Label about;
	int i = 0;
	Timeline tl;
	public ArrayList<String> Cars = new ArrayList<String>();
	// Change the way to read name of images .....

	// new String[] { "ambulance.png", "car1.png", "car2.png", "car3.png",
	// "car4.png", "police.png",
	// "truck1.png", "truck2.png", "cars.jpg" };
	final File images = new File("C:\\Users\\Mohamad Naji\\eclipse-workspace\\java\\parallel_Project1\\View\\images");

	@Override
	public void start(Stage primaryStage) {
		try {
			// remove fxml file ,we don't use it
			// loader = (AnchorPane)
			// FXMLLoader.load(getClass().getResource("/fxml/road.fxml"));
			loader = new AnchorPane();
			loader.setId("anchor");
			drawTL();
			System.out.println("start");
			listFilesForFolder(images);

			Scene scene = new Scene(loader, 1100, 700);
			scene.getStylesheets().add(getClass().getResource("/css/road.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Traffic Light Simulator");
			primaryStage.show();

			start.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					CreateCars();
					control();
					start.setDisable(true);
				}
			});

			pause.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					if (i == 0) {
						th1.suspend();
						th2.suspend();
						th3.suspend();
						StopCars();
						i++;
					} else {
						th1.resume();
						th2.resume();
						th3.resume();
						CreateCars();
						i--;
					}
				}
			});

			exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					System.exit(0);
				}
			});
			
//			to destry threads on exit
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					System.exit(0);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Read images name from "View/images" folder
	public void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				Cars.add(fileEntry.getName());
//				System.out.println("i am file ::::::::::::::::::\t" + fileEntry.getName());
			}
		}
	}

	private void control() {
		// we use thread names to suspend and start threads ,on pause button
		th1 = new Thread(new ColorControl(t1, t2, t3));
		th2 = new Thread(new ColorControl(t1, t2, t3));
		th3 = new Thread(new ColorControl(t1, t2, t3));
		th1.start();
		th2.start();
		th3.start();
	}

	public void drawTL() {

		r1 = new Road("E");
		r2 = new Road("S");
		r3 = new Road("W");

		t1 = new TrafficLight(780, 0, 200, 100, "E");
		t2 = new TrafficLight(715, 340, 100, 200, "S");
		t3 = new TrafficLight(298, 340, 200, 100, "W");

		t1.r = r1;
		t2.r = r2;
		t3.r = r3;

		start = new Button("Start");
		start.setLayoutX(50);
		start.setLayoutY(450);
		start.setPadding(new Insets(12, 45, 12, 45));

		pause = new Button("Pause");
		pause.setLayoutX(50);
		pause.setLayoutY(540);
		pause.setPadding(new Insets(12, 41, 12, 41));

		exit = new Button("Exit");
		exit.setLayoutX(50);
		exit.setLayoutY(630);
		exit.setPadding(new Insets(12, 52, 12, 52));
		
		about=new Label();
		about.setLayoutX(800);
		about.setLayoutY(440);
		//about.setMinWidth(250);
		about.setText("Done By: \nHassan Asaad\nMohamad Naji\n\nI3341\n\n2018 -2019\n\n");
		about.setTextAlignment(TextAlignment.CENTER);
		about.setPadding(new Insets(3, 20, 3, 20));

		loader.getChildren().add(start);
		loader.getChildren().add(pause);
		loader.getChildren().add(exit);
		
		loader.getChildren().add(about);
		
		loader.getChildren().add(t1.drawTrafficLight());
		loader.getChildren().add(t1.drawRedCircle());
		loader.getChildren().add(t1.drawYellowCircle());
		loader.getChildren().add(t1.drawGreenCircle());

		loader.getChildren().add(t2.drawTrafficLight());
		loader.getChildren().add(t2.drawRedCircle());
		loader.getChildren().add(t2.drawYellowCircle());
		loader.getChildren().add(t2.drawGreenCircle());

		loader.getChildren().add(t3.drawTrafficLight());
		loader.getChildren().add(t3.drawRedCircle());
		loader.getChildren().add(t3.drawYellowCircle());
		loader.getChildren().add(t3.drawGreenCircle());

	}

	public void manageRoad() {

		int turn = ThreadLocalRandom.current().nextInt(0, 2);
		if (Car.counter1 < 10) {
			String image = ChooseCar();
			Car car = new Car(image);
			car.AddRoad(t1.r);
			car.manageImage();
			if (turn == 0) {
				car.ELeft = true;
			} else {
				car.Forword = true;
			}
			if (t1.r.cars.size() > 0) {
				if (t1.Active) {
					while ((car = t1.r.cars.poll()) != null) {
						car.Move(car.xposition, car.yposition);
					}
					t1.r.distance[0] = 0;
					t1.r.distance[1] = 0;

				} else {
					car.Stop();
					loader.getChildren().add(car.iv1);
				}
			}
		} else {
			if (t1.r.cars.size() > 0) {
				Car car;
				if (t1.Active) {
					while ((car = t1.r.cars.poll()) != null) {
//						System.out.println("Size: " + t1.r.cars.size());
						car.Move(car.xposition, car.yposition);
					}
					t1.r.distance[0] = 0;
					t1.r.distance[1] = 0;
				}
			}
		}

		if (Car.counter < 10) {
			String image = ChooseCar();
			Car car = new Car(image);
			car.AddRoad(t3.r);

			if (turn == 0) {
				car.Forword = true;
			} else {
				car.Rit = true;
			}

			if (t3.r.cars.size() > 0) {
				if (t3.Active) {
					while ((car = t3.r.cars.poll()) != null) {
						car.Move(car.xposition, car.yposition);
					}
					t3.r.distance1[0] = 0;
					t3.r.distance1[1] = 0;
				} else {
					car.Stop();
					loader.getChildren().add(car.iv1);
				}
			}
		} else {
			if (t3.r.cars.size() > 0) {
				Car car;
				if (t3.Active) {
					while ((car = t3.r.cars.poll()) != null) {
//						System.out.println("Size: " + t3.r.cars.size());
						car.Move(car.xposition, car.yposition);
					}
					t3.r.distance1[0] = 0;
					t3.r.distance1[1] = 0;
				}
			}
		}

		if (Car.counter2 < 10) {
			String image = ChooseCar();
			Car car = new Car(image);
			car.AddRoad(t2.r);
			car.manageImage();
			if (turn == 0) {
				car.Left = true;
			} else {
				car.Rit = true;
			}

			if (t2.r.cars.size() > 0) {
				if (t2.Active) {
					while ((car = t2.r.cars.poll()) != null) {
						car.Move(car.xposition, car.yposition);
					}
					t2.r.distance2[0] = 0;
					t2.r.distance2[1] = 0;
				} else {
					car.Stop();
					loader.getChildren().add(car.iv1);
				}
			}
		} else {
			if (t2.r.cars.size() > 0) {
				Car car;
				if (t2.Active) {
					while ((car = t2.r.cars.poll()) != null) {
//						System.out.println("Size: " + t2.r.cars.size());
						car.Move(car.xposition, car.yposition);
					}
					t2.r.distance2[0] = 0;
					t2.r.distance2[1] = 0;
				}
			}
		}
	}

	public void CreateCars() {

		tl = new Timeline(new KeyFrame(Duration.seconds(2), ae -> {
			manageRoad();
		}));

		tl.setCycleCount(Animation.INDEFINITE);
		tl.play();

	}

	public void StopCars() {
		tl.pause();
	}

	public String ChooseCar() {

		String image;
		Random rand = new Random();
		int x = rand.nextInt(Cars.size());
//		System.out.println("iam rand\t" + x);
		// int index = ThreadLocalRandom.current().nextInt(0, 7+1);
		image = Cars.get(x);
		return image;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
