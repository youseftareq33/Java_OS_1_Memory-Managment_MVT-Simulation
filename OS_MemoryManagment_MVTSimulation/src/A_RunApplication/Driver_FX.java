package A_RunApplication;
	
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import B_Classes.Pcb;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class Driver_FX extends Application {
	
	//for data
	ArrayList<Pcb> q_jobs=new ArrayList<>();
	ArrayList<Pcb> q_ready=new ArrayList<>();
	List<Integer> al_time_of_process=new ArrayList<>();
	int memory_size=2048;
	int init_os_reserving=512;
	int os_reserving=init_os_reserving;
	int num_of_holes=0;
	int num_new_process=0;
	
	int time=0;
	ArrayList<String> al_job_finish=new ArrayList<>();
	ArrayList<String> al_job_brought_from_memory=new ArrayList<>();
	
	//for UI
	Stage stage = new Stage();
	DropShadow shadow = new DropShadow();
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			read_job_file(new File("src\\job.txt"));
			read_ready_file(new File("src\\ready.txt"));
			
			// scenes
			Scene scene = new Scene(mainPage());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			stage.setMaximized(true);
			stage.setScene(scene);
			stage.setTitle("MVT Simulation");
			stage.setIconified(true);
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/D2_Graphic_icons/os_app_icon.png")));
			stage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////
	// Pages:
	
	// main page
	public BorderPane mainPage() {
		BorderPane bp_startPage = new BorderPane();
		bp_startPage.setStyle("-fx-background-color:#7ac943;");
		
        Pane pane = new Pane();
        pane.setPrefSize(800, 600); 
        
		ScrollPane sp=new ScrollPane(pane);
		sp.setFitToWidth(true); 
		sp.setFitToHeight(true);
		bp_startPage.setCenter(sp);
		
		HBox hb=new HBox();
		
		hb.setSpacing(300);
		hb.setPadding(new Insets(0,353,0,0)); // top, right, bottom, left
		BorderPane bp = new BorderPane();
		bp.setStyle("-fx-background-color:#7ac943;");
		hb.getChildren().add(bp);
		hb.setStyle("-fx-background-color:#7ac943;");
		sp.setContent(hb);
		//#################################################################################
		// top
		
		HBox hb_top=new HBox();
		
			// left
			VBox vb_top_left=new VBox();
			
			HBox hb_t_top_left_top=new HBox();
			Text t_top_left_top=new Text("Job Queue");
			t_top_left_top.setStyle("-fx-font-size:15;-fx-font-weight:bold;");
			hb_t_top_left_top.getChildren().add(t_top_left_top);
			hb_t_top_left_top.setPadding(new Insets(0,0,10,148)); // top, right, bottom, left
			
			TableView<Pcb> tv_job_queue = update_job_queue_on_tableView(FXCollections.observableArrayList(q_jobs));
			
			
			vb_top_left.getChildren().addAll(hb_t_top_left_top,tv_job_queue);
			
			// right
			VBox vb_top_right=new VBox();
			
			HBox hb_t_top_right_top=new HBox();
			Text t_top_right_top=new Text("Ready Queue");
			t_top_right_top.setStyle("-fx-font-size:15;-fx-font-weight:bold;");
			hb_t_top_right_top.getChildren().add(t_top_right_top);
			hb_t_top_right_top.setPadding(new Insets(0,0,10,138)); // top, right, bottom, left
			
			TableView<Pcb> tv_ready_queue = update_ready_queue_on_tableView(FXCollections.observableArrayList(q_ready));
			
			vb_top_right.getChildren().addAll(hb_t_top_right_top,tv_ready_queue);
		
		hb_top.getChildren().addAll(vb_top_left,vb_top_right);
		hb_top.setSpacing(100);
		hb_top.setPadding(new Insets(10,0,0,350)); // top, right, bottom, left
		bp.setTop(hb_top);
		//#################################################################################
		// center
		
		VBox vb_center=new VBox();
		
			// center top
			VBox vb_center_top=new VBox();
			ScrollPane sp_center = new ScrollPane(vb_center_top);
			sp_center.setFitToWidth(true);
			sp_center.setFitToHeight(true);
			sp_center.setStyle("-fx-background-color: #7ac943;");
			sp_center.setPadding(new Insets(10,0,0,0)); // top, right, bottom, left
			
			HBox hb_center=new HBox();
			Text t_center=new Text("Memory");
			t_center.setStyle("-fx-font-size:18;-fx-font-weight:bold;");
			hb_center.getChildren().add(t_center);
			hb_center.setStyle("-fx-background-color:white;-fx-Border-color:black;-fx-Border-width:1;");
			hb_center.setPrefWidth(825);
			hb_center.setPrefHeight(400);
			hb_center.setAlignment(Pos.CENTER);
			
			vb_center_top.getChildren().add(hb_center);
			vb_center_top.setPadding(new Insets(10,0,0,350)); // top, right, bottom, left // 353 350
			vb_center_top.setStyle("-fx-background-color: #7ac943;");
			// center bottom
			VBox vb_bottom=new VBox();
			
			Text t_bottom_top=new Text("At time: -");
			t_bottom_top.setStyle("-fx-font-size:14;-fx-font-weight:bold;");
			
			Text t_bottom_center=new Text("job finished: -");
			t_bottom_center.setStyle("-fx-font-size:14;-fx-font-weight:bold;");
			
			Text t_bottom_bottom=new Text("job brought into memory: -");
			t_bottom_bottom.setStyle("-fx-font-size:14;-fx-font-weight:bold;");
			
			vb_bottom.getChildren().addAll(t_bottom_top,t_bottom_center,t_bottom_bottom);
			vb_bottom.setPadding(new Insets(10,0,20,350)); // top, right, bottom, left
			vb_bottom.setSpacing(7);
			vb_bottom.setStyle("-fx-background-color: #7ac943;");
		vb_center.getChildren().addAll(sp_center,vb_bottom);
		bp.setCenter(vb_center);
		
		////////////////////////////////////////////////////////////////////////////////////
		
		HBox hb_button=new HBox();
		
		// start button
		Button b_start=new Button("Start");
		b_start.setPrefSize(150, 40);
		b_start.setStyle("-fx-background-color: LAWNGREEN; -fx-background-radius: 12; -fx-Border-color: OLIVEDRAB;"
				+ "-fx-Border-radius: 10;-fx-font-size:20;-fx-Border-width:2;-fx-font-weight:bold;");
		
		b_start.addEventHandler(MouseEvent.MOUSE_ENTERED,
		        new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent e) {
	        	  b_start.setEffect(shadow);
	        	  b_start.setStyle("-fx-background-color: LAWNGREEN;-fx-Border-color: black;-fx-font-size:20;"
	        			  + "-fx-Border-width:4;-fx-font-weight:bold;-fx-text-fill: white;-fx-background-radius: 12;-fx-Border-radius: 8");
	        	  hb_button.setPadding(new Insets(0,0,20,0)); // top, right, bottom, left
	          }
	    });
		
		b_start.addEventHandler(MouseEvent.MOUSE_EXITED,
		        new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent e) {
	        	  b_start.setEffect(null);
	        	  b_start.setStyle("-fx-background-color: LAWNGREEN; -fx-background-radius: 12; -fx-Border-color: OLIVEDRAB;"
	    			   	  + "-fx-Border-radius: 10;-fx-font-size:20;-fx-Border-width:2;-fx-font-weight:bold;");
	        	  hb_button.setPadding(new Insets(0,0,23,0)); // top, right, bottom, left
	          }
	    });
		
		
		// next button
		Button b_next=new Button("Next");
		b_next.setPrefSize(150, 40);
		b_next.setStyle("-fx-background-color: LAWNGREEN; -fx-background-radius: 12; -fx-Border-color: OLIVEDRAB;"
				+ "-fx-Border-radius: 10;-fx-font-size:20;-fx-Border-width:2;-fx-font-weight:bold;");
		
		b_next.addEventHandler(MouseEvent.MOUSE_ENTERED,
		        new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent e) {
	        	  b_next.setEffect(shadow);
	        	  b_next.setStyle("-fx-background-color: LAWNGREEN;-fx-Border-color: black;-fx-font-size:20;"
	        			  + "-fx-Border-width:4;-fx-font-weight:bold;-fx-text-fill: white;-fx-background-radius: 12;-fx-Border-radius: 8");
	        	  hb_button.setPadding(new Insets(0,0,20,0)); // top, right, bottom, left
	          }
	    });
		
		b_next.addEventHandler(MouseEvent.MOUSE_EXITED,
		        new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent e) {
	        	  b_next.setEffect(null);
	        	  b_next.setStyle("-fx-background-color: LAWNGREEN; -fx-background-radius: 12; -fx-Border-color: OLIVEDRAB;"
	    			   	  + "-fx-Border-radius: 10;-fx-font-size:20;-fx-Border-width:2;-fx-font-weight:bold;");
	        	  hb_button.setPadding(new Insets(0,0,23,0)); // top, right, bottom, left
	          }
	    });
		
		// compaction button
		Button b_compaction=new Button("Compaction");
		b_compaction.setPrefSize(150, 40);
		b_compaction.setStyle("-fx-background-color: red; -fx-background-radius: 12; -fx-Border-color: #800000;"
				+ "-fx-Border-radius: 10;-fx-font-size:20;-fx-Border-width:2;-fx-font-weight:bold;-fx-text-fill: black;");
		
		b_compaction.addEventHandler(MouseEvent.MOUSE_ENTERED,
		        new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent e) {
	        	  b_compaction.setEffect(shadow);
	        	  b_compaction.setStyle("-fx-background-color: red;-fx-Border-color: black;-fx-font-size:20;"
	        			  + "-fx-Border-width:4;-fx-font-weight:bold;-fx-text-fill: white;-fx-background-radius: 12;-fx-Border-radius: 8");
	        	  hb_button.setPadding(new Insets(0,0,20,0)); // top, right, bottom, left
	          }
	    });
		
		b_compaction.addEventHandler(MouseEvent.MOUSE_EXITED,
		        new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent e) {
	        	  b_compaction.setEffect(null);
	        	  b_compaction.setStyle("-fx-background-color: red; -fx-background-radius: 12; -fx-Border-color: #800000;"
	    			   	  + "-fx-Border-radius: 10;-fx-font-size:20;-fx-Border-width:2;-fx-font-weight:bold;-fx-text-fill: black;");
	        	  hb_button.setPadding(new Insets(0,0,23,0)); // top, right, bottom, left
	          }
	    });
		
		hb_button.getChildren().add(b_start);
		hb_button.setAlignment(Pos.CENTER);
		hb_button.setPadding(new Insets(0,0,23,0)); // top, right, bottom, left
		
		
		
		// set on action:
		
		// start
		b_start.setOnAction(e->{
			
			if(q_ready.isEmpty() && !q_jobs.isEmpty()) {
				// to set process on ready queue from job queue
				for(int i=0;i<q_jobs.size();i++) {
					
					if(os_reserving+q_jobs.get(i).getSize()<=memory_size) { // if (os reserving from memory + the job size on job queue) less or equal (memory size)
						Pcb chossen_process=q_jobs.get(i); // store this process on chossen_process
						
						al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
						q_jobs.remove(chossen_process); // remove this process from job queue
						i--; // cause q_jobs(list) size decrement
						
						chossen_process.setBase_register(os_reserving); // set base register for this process (os reserving)
						chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this process (base register for process + process size)
						al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
						q_ready.add(chossen_process); // add this process to ready queue
							
						os_reserving+=chossen_process.getSize(); // update os reserving with the process size
					}
				}
				
				if(q_ready.isEmpty() && !q_jobs.isEmpty()) {
					t_center.setText("The memory size cannot handel the processes size");
					System.out.println("The memory size cannot handel the processes size");
					hb_button.getChildren().clear();
				}
				else {
					// sort the time of process
					Collections.sort(al_time_of_process);
					
					hb.getChildren().clear();
					hb.getChildren().add(bp_steps());
					
					al_job_brought_from_memory.clear();
					al_job_finish.clear();
					
					hb_button.getChildren().clear();
					hb_button.getChildren().add(b_next);
				}
			}
			else if(!q_ready.isEmpty()){
				hb.getChildren().clear();
				hb.getChildren().add(bp_steps());
				
				al_job_brought_from_memory.clear();
				al_job_finish.clear();
				
				hb_button.getChildren().clear();
				hb_button.getChildren().add(b_next);
			}
			
			
			
		});
		
		
		// next
		b_next.setOnAction(e->{
			
			al_job_brought_from_memory.clear();
			al_job_finish.clear();
			
			// while there is a processes inside queue ready or queue job
			if(!q_ready.isEmpty() || !q_jobs.isEmpty()) {
				
				// calculate number of holes
				calc_num_of_holes();
				
				System.out.println(q_ready.toString());
				System.out.println("num of holes: "+num_of_holes);
				System.out.println(al_time_of_process.toString());
				System.out.println("============================================\n\n");
				
				
				// if number of holes be greater than 3 --> make compaction (re-allocate to jobs/process that inside ready queue (q_ready))
				if(num_of_holes>3) {

					hb_button.getChildren().clear();
					hb_button.getChildren().add(b_compaction);
						

                    GaussianBlur blur = new GaussianBlur(6);
                    bp_startPage.setEffect(blur);
                    stage.getScene().setRoot(bp_startPage);

                    Stage stage_alert = new Stage();
                    stage_alert.setTitle("Alert");
                    stage_alert.getIcons().add(new Image(getClass().getResourceAsStream("/D2_Graphic_icons/alert.png")));
                    
                    stage_alert.initStyle(StageStyle.DECORATED);
                    stage_alert.initModality(Modality.APPLICATION_MODAL);
                    stage_alert.initOwner(stage.getScene().getWindow());
                    stage_alert.setWidth(700);
                    stage_alert.setOnCloseRequest(e_close -> {
                    	stage_alert.close();
                        bp_startPage.setEffect(null);
                    });
                    
                    VBox vb_message = new VBox();
                    vb_message.setSpacing(100);
                    vb_message.setPrefSize(500, 200);
                    vb_message.setAlignment(Pos.CENTER);
                    vb_message.setStyle("-fx-background-color: red;");
                    
                    Text t_message = new Text("The memory need to compaction!");
                    t_message.setStyle("-fx-font-size: 17; -fx-font-weight: bold;-fx-text-fill: white;");
                    Button b_close = new Button("Okay");
                    b_close.setEffect(shadow);
                    b_close.setStyle("-fx-background-color: red;-fx-Border-color: black;-fx-font-size:12;"
  	        			  + "-fx-Border-width:4;-fx-font-weight:bold;-fx-text-fill: white;-fx-background-radius: 12;-fx-Border-radius: 8");
                    b_close.setPrefSize(80, 20);
                    b_close.setOnAction(e_close-> {
                    	stage_alert.close();
                        bp_startPage.setEffect(null);
                    });

                    vb_message.getChildren().addAll(t_message, b_close);
                    Scene newScene = new Scene(vb_message, 300, 200);
                    stage_alert.setScene(newScene);
                    stage_alert.show();
                    
                    Platform.runLater(() -> {
						sp.layout();
			            sp.setHvalue(sp.getHmax()); // scroll to the right
					});
				
                    // to set process on ready queue from job queue
    				for(int i=0;i<q_jobs.size();i++) {
    					
    					if(os_reserving+q_jobs.get(i).getSize()<=memory_size) { // if (os reserving from memory + the job size on job queue) less or equal (memory size)
    						Pcb chossen_process=q_jobs.get(i); // store this process on chossen_process
    						
    						al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
    						q_jobs.remove(chossen_process); // remove this process from job queue
    						i--; // cause q_jobs(list) size decrement
    						
    						chossen_process.setBase_register(os_reserving); // set base register for this process (os reserving)
    						chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this process (base register for process + process size)
    						al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
    						q_ready.add(chossen_process); // add this process to ready queue
    							
    						os_reserving+=chossen_process.getSize(); // update os reserving with the process size
    					}
    				}
				}
				// else number of holes less or equal 3
				else {
					

					for(int i=0;i<q_ready.size();i++) {
						// if the ready queue has more than one process
						if(q_ready.size()>1) {
							q_ready.get(i).setTime_in_memory(q_ready.get(i).getTime_in_memory()-al_time_of_process.get(0)); // decrement time by (the process that has less time)
						}
						// else if the ready queue has just one process
						else if(q_ready.size()==1) {
							// if the process not first process and don't go out of method to print(in case p1-->10 time && p2-->5 time) ##### just for print cause doesn't go outside the method to print ####
							if(i!=0) {
								// calculate number of holes
								calc_num_of_holes();
								
								al_time_of_process.clear();
								al_time_of_process.add(q_ready.get(i).getTime_in_memory());
								
								System.out.println(q_ready.toString());
								System.out.println("num of holes: "+num_of_holes);
								System.out.println(al_time_of_process.toString());
								System.out.println("============================================\n\n");
							}
							
							q_ready.get(i).setTime_in_memory(0); // decrement time by (this process)
						}
					}
					
					
					first_fit();
					
					// for fx
					if(!al_time_of_process.isEmpty()) {
						time+=al_time_of_process.get(0);
					}

					hb.getChildren().add(bp_steps());

					
					
					// decrement time of process on al_time_of_process (without new process)
					for(int i=1;i<al_time_of_process.size()-num_new_process;i++) { 
						al_time_of_process.set(i, (al_time_of_process.get(i)-al_time_of_process.get(0)));
					}
					
					// remove the time of less process time (5,0,0)
					al_time_of_process.remove(al_time_of_process.get(0)); 
					
					// if there are another processes times finish with the first process (0,0,..)
					for(int i=0;i<al_time_of_process.size();i++) {
						if(al_time_of_process.get(i)==0) {
							al_time_of_process.remove(al_time_of_process.get(i)); // remove the time of less process time
							i--; // cause al_time_of_process(list) size decrement
						}
					}
					
					num_new_process=0; // rest number of new process to 0
					// sort the time of process
					Collections.sort(al_time_of_process);
					
				}
			}
			
			// memory finish job
			if(q_jobs.isEmpty() && q_ready.isEmpty()) {

				hb.getChildren().remove(hb.getChildren().size()-1);
				hb.getChildren().add(bp_steps());
				hb_button.getChildren().clear();

				// calculate number of holes
				calc_num_of_holes();
				
				System.out.println("ready queue: "+q_ready.toString());
				System.out.println("job queue: "+q_jobs.toString());
				System.out.println("num of holes: "+num_of_holes+"\n");
				
				System.out.println("--- "+"Memory is empty :)"+" ---");
				
			}
			
			
		
		});
		
		
		
		// compaction
		b_compaction.setOnAction(e->{
			compactions();
			
			// for fx
			hb.getChildren().add(bp_steps());

			
			
			hb_button.getChildren().clear();
			hb_button.getChildren().add(b_next);
			
			Platform.runLater(() -> {
				sp.layout();
	            sp.setHvalue(sp.getHmax()); // scroll to the right
			});
			
		});
		
		
		bp_startPage.setBottom(hb_button);
		
		return bp_startPage;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////
	// Methods:
	
	// for Data
	
	//-- Read Jobs from job.txt and set it's process(jobs) inside job queue (q_jobs)
	public void read_job_file(File file) {
		
		// check if the file exist
		if(!file.exists()) {
			System.out.println("there isn't job file !!!");
		}
		else {
			
			try {
				// get data thats inside the file.
				Scanner in = new Scanner(file);
				String s_insideLine[] = null;
				
				while(in.hasNextLine()) {
					String line=in.nextLine().trim(); // store line while trim(remove first and end spaces)
					s_insideLine=line.split("\\s+"); // take the line as tokens like below:
					
					/*
					 * Integer.parseInt(s_insideLine[0]) : process id      token(1)
					 * Integer.parseInt(s_insideLine[1]) : memory needed   token(2)
					 * Integer.parseInt(s_insideLine[2]) : time in memory  token(3)
					 */
					
					Pcb pcb=new Pcb(Integer.parseInt(s_insideLine[0]),Integer.parseInt(s_insideLine[1]),Integer.parseInt(s_insideLine[2]),0,0);
					q_jobs.add(pcb); // add the process on queue job (q_jobs)
				}
				
				in.close();
			}
			catch(Exception e) {
				e.getMessage();
			}
			
			
			
		}
	}
	
	//-- Read Jobs from ready.txt and set it's process(jobs) inside ready queue (q_ready)
	public void read_ready_file(File file) {
			
		// check if the file exist
		if(!file.exists()) {
			System.out.println("there isn't ready file !!!");
		}
		else {
			
			try {
				// get data thats inside the file.
				Scanner in = new Scanner(file);
				String s_insideLine[] = null;
				
				while(in.hasNextLine()) {
					String line=in.nextLine().trim(); // store line while trim(remove first and end spaces)
					s_insideLine=line.split("\\s+"); // take the line as tokens like below:
					
					/*
					 * Integer.parseInt(s_insideLine[0]) : process id      token(1)
					 * Integer.parseInt(s_insideLine[1]) : memory needed   token(2)
					 * Integer.parseInt(s_insideLine[2]) : time in memory  token(3)
					 */
					
					Pcb pcb=new Pcb(Integer.parseInt(s_insideLine[0]),Integer.parseInt(s_insideLine[1]),Integer.parseInt(s_insideLine[2]),0,0);
					pcb.setBase_register(os_reserving); // set base register for this process (os reserving)
					pcb.setLimit_register(pcb.getBase_register()+pcb.getSize()); // set limit register for this process (base register for process + process size)
					al_time_of_process.add(pcb.getTime_in_memory()); // add time of process on al_time_of_process
					q_ready.add(pcb); // add this process to ready queue
						
					os_reserving+=pcb.getSize(); // update os reserving with the process size
					
				}
				
				if(!q_ready.isEmpty()) {
					// sort the time of process
					Collections.sort(al_time_of_process);
					
					// if there is remaining memory unused then it's a hole
					if(os_reserving<memory_size) {
						num_of_holes++; // increment number of holes
					}
				}
				
				
				in.close();
			}
			catch(Exception e) {
				e.getMessage();
			}
			
			
			
		}
	}
		
	//-- (compaction) re-allocate to jobs that inside ready queue (q_ready)
	public void compactions() {
		
		for(int i=0;i<q_ready.size();i++) {
			// if the first process has previous hole
			if(i==0 && q_ready.get(i).getBase_register()!=init_os_reserving) {
				q_ready.get(i).setBase_register(init_os_reserving); // set base register for this process (init_os_reserving)
				q_ready.get(i).setLimit_register(q_ready.get(i).getBase_register()+q_ready.get(i).getSize());  // set limit register for this process (base register for process + process size)
			}
			else if(i>0) {
				q_ready.get(i).setBase_register(q_ready.get(i-1).getLimit_register()); // set base register for this process (limit register for previous process)
				q_ready.get(i).setLimit_register(q_ready.get(i).getBase_register()+q_ready.get(i).getSize()); // set limit register for this process (base register for process + process size)
			}
			
			num_of_holes=1;
		}
		
	}
		
	//-- calculate number of holes on memory
	public void calc_num_of_holes() {
		num_of_holes=0;
		if(q_ready.isEmpty()) {
			num_of_holes=1;
		}
		else {
			for(int i=0;i<q_ready.size();i++) {	
				// first process on ready queue
				if(i==0) {
					// if process has previous hole
					if(q_ready.get(i).getBase_register()>init_os_reserving) {
						num_of_holes++;
					}
					// if process has hole next of it
					if(q_ready.size()==1 && q_ready.get(i).getLimit_register()<memory_size) {
						num_of_holes++;
					}
					// if process has hole next of it (hole between two process)
					if(q_ready.size()>1 && q_ready.get(i).getLimit_register()<q_ready.get(i+1).getBase_register()) {
						num_of_holes++;
					}
				}
				// middle process's on ready queue
				else if(q_ready.size()>1 && i>0 && i!=q_ready.size()-1) {
					// if process has hole next of it
					if(q_ready.get(i).getLimit_register()<q_ready.get(i+1).getBase_register()) {
						num_of_holes++;
					}
				}
				// final process on ready queue
				else if(q_ready.size()>1 && i==q_ready.size()-1) {
					// if process has hole next of it
					if(q_ready.get(i).getLimit_register()<memory_size) {
						num_of_holes++;
					}
				}
			}
		}
		
	}
	
	//-- choose process in first fit (the first process from job queue that fit with hole)
	public void first_fit() {
		
		for(int i=0;i<q_ready.size();i++) {
			// if the (time in memory) of process on ready queue equal 0 --> (the process is executed/finished)
			if(q_ready.get(i).getTime_in_memory()==0) {
				int hole_size=q_ready.get(i).getSize(); // set hole size as the process that executed/finished size
				al_job_finish.add("p"+q_ready.get(i).getProcess_id()); // add the job that finish
				
				// if the first process in ready queue (executed/finished)
				if(i==0 && q_ready.size()>1) {
					// if the first process in ready queue has previous hole (may the first process in middle of the memory)
					if(q_ready.get(i).getBase_register()>init_os_reserving) {
						// add size of this hole on hole_size
						hole_size+=(q_ready.get(i).getBase_register()-init_os_reserving);
					}
					
					// if the first process in ready queue has hole next of it
					if(q_ready.get(i).getLimit_register()<q_ready.get(i+1).getBase_register()) {
						// add size of this hole on hole_size
						hole_size+=(q_ready.get(i+1).getBase_register()-q_ready.get(i).getLimit_register());
					}
					
					// ** using first fit **
					boolean find_process=false;
					int new_process_reserving_os=init_os_reserving;
					// search on process on queue job to put it inside queue ready alternative of the process that executed/finished
					for(int j=0;j<q_jobs.size();j++) {
						
						// if the new process from job queue less or equal the process that executed/finished (can be set alternative of the process that executed/finished)
						if(q_jobs.get(j).getSize()<=hole_size) {
							Pcb chossen_process=q_jobs.get(j); // store this new process on chossen_process
							
							os_reserving-=q_ready.get(i).getSize(); // remove the process that executed/finished size from os reserving 
							al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
							q_jobs.remove(chossen_process); // remove the new from job queue
							j--; // cause q_jobs(list) size decrement
							
							chossen_process.setBase_register(new_process_reserving_os); // set base register for this new process (new_process_reserving_os)
							chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this new process (base register for process + process size)
							al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
							num_new_process++; // increment number of new process
							
							// if there isn't process set in hole
							if(find_process==false) {
								q_ready.set(i, chossen_process); // add this new process to ready queue at the same position of process that executed/finished
							}
							// if there is process set in hole (p=1000 finish, p1=500 and p2=600)
							else {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue next of position of process that executed/finished
								i++; // cause q_ready(list) size increment
							}
							
							hole_size-=chossen_process.getSize();
							new_process_reserving_os+=chossen_process.getSize();
							os_reserving+=chossen_process.getSize(); // update os reserving with the new process size
							find_process=true;
							
						}
						
					}
					
					// if doesn't find process alternative of the hole
					if(find_process==false) {
						os_reserving-=q_ready.get(i).getSize(); // remove the process that executed/finished size from os reserving 
						q_ready.remove(q_ready.get(i)); // remove the process that executed/finished from ready queue
						i--; // cause q_ready(list) size decrement
					}
					
				}
				// else if the middle process in ready queue (executed/finished)
				else if(i>0 && i<q_ready.size()-1) {
					// if the middle process in ready queue has previous hole
					if(q_ready.get(i).getBase_register()<q_ready.get(i-1).getLimit_register()) {
						// add size of this hole on hole_size
						hole_size+=(q_ready.get(i-1).getLimit_register()-q_ready.get(i).getBase_register());
					}
					
					// if the middle process in ready queue has hole next of it
					if(q_ready.get(i).getLimit_register()<q_ready.get(i+1).getBase_register()) {
						// add size of this hole on hole_size
						hole_size+=(q_ready.get(i+1).getBase_register()-q_ready.get(i).getLimit_register());
					}
					
					
					// ** using first fit **
					boolean find_process=false;
					int new_process_reserving_os=q_ready.get(i-1).getLimit_register();
					// search on process on queue job to put it inside queue ready alternative of the process that executed/finished
					for(int j=0;j<q_jobs.size();j++) {
						
						// if the new process from job queue less or equal the process that executed/finished (can be set alternative of the process that executed/finished)
						if(q_jobs.get(j).getSize()<=hole_size) {
							Pcb chossen_process=q_jobs.get(j); // store this new process on chossen_process
							
							os_reserving-=q_ready.get(i).getSize(); // remove the process that executed/finished size from os reserving 
							al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
							q_jobs.remove(chossen_process); // remove the new from job queue
							j--; // cause q_jobs(list) size decrement
							
							chossen_process.setBase_register(new_process_reserving_os); // set base register for this new process (new_process_reserving_os)
							chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this new process (base register for process + process size)
							al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
							num_new_process++; // increment number of new process
							
							// if there isn't process set in hole
							if(find_process==false) {
								q_ready.set(i, chossen_process); // add this new process to ready queue at the same position of process that executed/finished
							}
							// if there is process set in hole (p=1000 finish, p1=500 and p2=600)
							else {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue next of position of process that executed/finished
								i++; // cause q_ready(list) size increment
							}
							
							hole_size-=chossen_process.getSize();
							new_process_reserving_os+=chossen_process.getSize();
							os_reserving+=chossen_process.getSize(); // update os reserving with the new process size
							find_process=true;
							
						}
						
					}
					
					// if doesn't find process alternative of the hole
					if(find_process==false) {
						os_reserving-=q_ready.get(i).getSize(); // remove the process that executed/finished size from os reserving 
						q_ready.remove(q_ready.get(i)); // remove the process that executed/finished from ready queue
						i--; // cause q_ready(list) size decrement
					}
				}
				// else if the final process in ready queue (executed/finished)
				else if(i==q_ready.size()-1 && q_ready.size()>1) {
					// if the final process in ready queue has previous hole
					if(q_ready.get(i).getBase_register()<q_ready.get(i-1).getLimit_register()) {
						// add size of this hole on hole_size
						hole_size+=(q_ready.get(i-1).getLimit_register()-q_ready.get(i).getBase_register());
					}
					
					// if the final process in ready queue has next hole
					if(q_ready.get(i).getLimit_register()<memory_size) {
						// add size of this hole on hole_size
						hole_size+=memory_size-q_ready.get(i).getLimit_register();
					}
					
					
					// ** using first fit **
					boolean find_process=false;
					int new_process_reserving_os=q_ready.get(i-1).getLimit_register();
					// search on process on queue job to put it inside queue ready alternative of the process that executed/finished
					for(int j=0;j<q_jobs.size();j++) {
						
						// if the new process from job queue less or equal the process that executed/finished (can be set alternative of the process that executed/finished)
						if(q_jobs.get(j).getSize()<=hole_size) {
							Pcb chossen_process=q_jobs.get(j); // store this new process on chossen_process
							
							os_reserving-=q_ready.get(i).getSize(); // remove the process that executed/finished size from os reserving 
							al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
							q_jobs.remove(chossen_process); // remove the new from job queue
							j--; // cause q_jobs(list) size decrement
							
							chossen_process.setBase_register(new_process_reserving_os); // set base register for this new process (new_process_reserving_os)
							chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this new process (base register for process + process size)
							al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
							num_new_process++; // increment number of new process
							
							// if there isn't process set in hole
							if(find_process==false) {
								q_ready.set(i, chossen_process); // add this new process to ready queue at the same position of process that executed/finished
							}
							// if there is process set in hole (p=1000 finish, p1=500 and p2=600)
							else {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue next of position of process that executed/finished
								i++; // cause q_ready(list) size increment
							}
							
							hole_size-=chossen_process.getSize();
							new_process_reserving_os+=chossen_process.getSize();
							os_reserving+=chossen_process.getSize(); // update os reserving with the new process size
							find_process=true;
							
						}
						
					}
					
					// if doesn't find process alternative of the hole
					if(find_process==false) {
						os_reserving-=q_ready.get(i).getSize(); // remove the process that executed/finished size from os reserving 
						q_ready.remove(q_ready.get(i)); // remove the process that executed/finished from ready queue
						i--; // cause q_ready(list) size decrement
					}
					
				}
				// else if the ready queue has just one process on it and the process is (executed/finished)
				else if(i==0 && q_ready.size()==1) {
					os_reserving-=q_ready.get(i).getSize(); // remove the process that executed/finished size from os reserving 
					q_ready.remove(q_ready.get(i)); // remove the process that executed/finished from ready queue
					
					hole_size=memory_size-init_os_reserving;
					
					// if the ready queue has just one process on it and there is no process on job queue
					if(!q_jobs.isEmpty()) {
						// ** using first fit **
						boolean find_process=false;
						int new_process_reserving_os=init_os_reserving;
						// search on process on queue job to put it inside queue ready alternative of the process that executed/finished
						for(int j=0;j<q_jobs.size();j++) {
							
							// if the new process from job queue less or equal the process that executed/finished (can be set alternative of the process that executed/finished)
							if(q_jobs.get(j).getSize()<=hole_size) {
								Pcb chossen_process=q_jobs.get(j); // store this new process on chossen_process
								
								al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
								q_jobs.remove(chossen_process); // remove the new from job queue
								j--; // cause q_jobs(list) size decrement
								
								chossen_process.setBase_register(new_process_reserving_os); // set base register for this new process (new_process_reserving_os)
								chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this new process (base register for process + process size)
								al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
								num_new_process++; // increment number of new process
								
								// if there isn't process set in hole
								if(find_process==false) {
//									q_ready.set(0, chossen_process); // add this new process to ready queue at the same position of process that executed/finished
									q_ready.add(chossen_process);
								}
								// if there is process set in hole (p=1000 finish, p1=500 and p2=600)
								else {
									q_ready.add(i+1, chossen_process); // add this new process to ready queue next of position of process that executed/finished
									i++; // cause q_ready(list) size increment
								}
								
								hole_size-=chossen_process.getSize();
								new_process_reserving_os+=chossen_process.getSize();
								os_reserving+=chossen_process.getSize(); // update os reserving with the new process size
								find_process=true;
								
							}
							
						}
						
						// if doesn't find process alternative of the hole
						if(find_process==false) {
							System.out.println("The memory size cannot handel the processes size");
							System.exit(0);
						}
					}
					
					
				}
			}
			// play on ready queue (ready queue has two process, and job queue has processes can fit with hole in ready queue)
			else {
				// if the first process in ready queue has previous hole (may the first process in middle of the memory)
				if(i==0 && q_ready.get(i).getBase_register()>init_os_reserving) {
					// add size of this hole on hole_size
					int hole_size=(q_ready.get(i).getBase_register()-init_os_reserving);
					// ** using first fit **
					boolean find_process=false;
					int new_process_reserving_os=init_os_reserving;
					// search on process on job queue
					for(int j=0;j<q_jobs.size();j++) {
						
						// if the new process from job queue less or equal the hole
						if(q_jobs.get(j).getSize()<=hole_size) {
							Pcb chossen_process=q_jobs.get(j); // store this new process on chossen_process
							
							al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
							q_jobs.remove(chossen_process); // remove the new process from job queue
							j--; // cause q_jobs(list) size decrement
							
							chossen_process.setBase_register(new_process_reserving_os); // set base register for this new process (new_process_reserving_os)
							chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this new process (base register for process + process size)
							al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
							num_new_process++; // increment number of new process
							
							// if there isn't process set in hole
							if(find_process==false) {
								q_ready.add(i, chossen_process); // add this new process to ready queue on hole position;
								i++; // cause q_ready(list) size increment
							}
							// if there is process set in hole (p=1000 finish, p1=500 and p2=600)
							else {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue next of position of process that on hole
								i++; // cause q_ready(list) size increment
							}
							
							hole_size-=chossen_process.getSize();
							new_process_reserving_os+=chossen_process.getSize();
							os_reserving+=chossen_process.getSize(); // update os reserving with the new process size
							find_process=true;
						}
						
					}
				}
					
				// else if the middle process in ready queue has previous hole
				else if(i>0 && i<q_ready.size()-1 && q_ready.get(i).getBase_register()>q_ready.get(i-1).getLimit_register()) {
					// store size of this hole on hole_size
					int hole_size=q_ready.get(i).getBase_register()-q_ready.get(i-1).getLimit_register();
					
					// ** using first fit **
					boolean find_process=false;
					int new_process_reserving_os=q_ready.get(i-1).getLimit_register();
					// search on process on job queue
					for(int j=0;j<q_jobs.size();j++) {
						
						// if the new process from job queue less or equal the hole
						if(q_jobs.get(j).getSize()<=hole_size) {
							Pcb chossen_process=q_jobs.get(j); // store this new process on chossen_process
							
							al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
							q_jobs.remove(chossen_process); // remove the new process from job queue
							j--; // cause q_jobs(list) size decrement
							
							chossen_process.setBase_register(new_process_reserving_os); // set base register for this new process (new_process_reserving_os)
							chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this new process (base register for process + process size)
							al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
							num_new_process++; // increment number of new process
							
							// if there isn't process set in hole
							if(find_process==false) {
								q_ready.add(i, chossen_process); // add this new process to ready queue on hole position;
								i++; // cause q_ready(list) size increment
							}
							// if there is process set in hole (p=1000 finish, p1=500 and p2=600)
							else {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue next of position of process that on hole
								i++; // cause q_ready(list) size increment
							}
							
							hole_size-=chossen_process.getSize();
							new_process_reserving_os+=chossen_process.getSize();
							os_reserving+=chossen_process.getSize(); // update os reserving with the new process size
							find_process=true;
							
						}
						
					}
					
				}
				// else if the middle process in ready queue has hole next of it
				else if(i>0 && i<q_ready.size()-1 && q_ready.get(i).getLimit_register()<q_ready.get(i+1).getBase_register()) {
					// store size of this hole on hole_size
					int hole_size=q_ready.get(i).getLimit_register()-q_ready.get(i+1).getBase_register();
					
					// ** using first fit **
					boolean find_process=false;
					int new_process_reserving_os=q_ready.get(i).getLimit_register();
					// search on process on job queue
					for(int j=0;j<q_jobs.size();j++) {
						
						// if the new process from job queue less or equal the hole
						if(q_jobs.get(j).getSize()<=hole_size) {
							Pcb chossen_process=q_jobs.get(j); // store this new process on chossen_process
							
							al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
							q_jobs.remove(chossen_process); // remove the new process from job queue
							j--; // cause q_jobs(list) size decrement
							
							chossen_process.setBase_register(new_process_reserving_os); // set base register for this new process (new_process_reserving_os)
							chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this new process (base register for process + process size)
							al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
							num_new_process++; // increment number of new process
							
							// if there isn't process set in hole
							if(find_process==false) {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue on hole position;
								i++; // cause q_ready(list) size increment
							}
							// if there is process set in hole (p=1000 finish, p1=500 and p2=600)
							else {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue next of position of process that on hole
								i++; // cause q_ready(list) size increment
							}
							
							hole_size-=chossen_process.getSize();
							new_process_reserving_os+=chossen_process.getSize();
							os_reserving+=chossen_process.getSize(); // update os reserving with the new process size
							find_process=true;
							
						}
						
					}
					
				}
				// else if the final process in ready queue has previous hole
				else if(i==q_ready.size()-1 && q_ready.get(i).getBase_register()>q_ready.get(i-1).getLimit_register()) {
					// store size of this hole on hole_size
					int hole_size=q_ready.get(i).getBase_register()-q_ready.get(i-1).getLimit_register();
					
					// ** using first fit **
					boolean find_process=false;
					int new_process_reserving_os=q_ready.get(i-1).getLimit_register();
					// search on process on job queue
					for(int j=0;j<q_jobs.size();j++) {
						
						// if the new process from job queue less or equal the hole
						if(q_jobs.get(j).getSize()<=hole_size) {
							Pcb chossen_process=q_jobs.get(j); // store this new process on chossen_process
							
							al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
							q_jobs.remove(chossen_process); // remove the new process from job queue
							j--; // cause q_jobs(list) size decrement
							
							chossen_process.setBase_register(new_process_reserving_os); // set base register for this new process (new_process_reserving_os)
							chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this new process (base register for process + process size)
							al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
							num_new_process++; // increment number of new process
							
							// if there isn't process set in hole
							if(find_process==false) {
								q_ready.add(i, chossen_process); // add this new process to ready queue on hole position;
								i++; // cause q_ready(list) size increment
							}
							// if there is process set in hole (p=1000 finish, p1=500 and p2=600)
							else {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue next of position of process that on hole
								i++; // cause q_ready(list) size increment
							}
							
							hole_size-=chossen_process.getSize();
							new_process_reserving_os+=chossen_process.getSize();
							os_reserving+=chossen_process.getSize(); // update os reserving with the new process size
							find_process=true;
							
						}
						
					}
					
				}
				// else if the final process in ready queue has next hole
				else if(i==q_ready.size()-1 && q_ready.get(i).getLimit_register()<memory_size) {
					// store size of this hole on hole_size
					int hole_size=memory_size-q_ready.get(i).getLimit_register();
					
					// ** using first fit **
					boolean find_process=false;
					int new_process_reserving_os=q_ready.get(i).getLimit_register();
					// search on process on job queue
					for(int j=0;j<q_jobs.size();j++) {
						
						// if the new process from job queue less or equal the hole
						if(q_jobs.get(j).getSize()<=hole_size) {
							Pcb chossen_process=q_jobs.get(j); // store this new process on chossen_process
							
							al_job_brought_from_memory.add("p"+chossen_process.getProcess_id()); // add the job that brought from memory
							q_jobs.remove(chossen_process); // remove the new process from job queue
							j--; // cause q_jobs(list) size decrement
							
							chossen_process.setBase_register(new_process_reserving_os); // set base register for this new process (new_process_reserving_os)
							chossen_process.setLimit_register(chossen_process.getBase_register()+chossen_process.getSize()); // set limit register for this new process (base register for process + process size)
							al_time_of_process.add(chossen_process.getTime_in_memory()); // add time of process on al_time_of_process
							num_new_process++; // increment number of new process
							
							// if there isn't process set in hole
							if(find_process==false) {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue on hole position;
								i++; // cause q_ready(list) size increment
							}
							// if there is process set in hole (p=1000 finish, p1=500 and p2=600)
							else {
								q_ready.add(i+1, chossen_process); // add this new process to ready queue next of position of process that on hole
								i++; // cause q_ready(list) size increment
							}
							
							hole_size-=chossen_process.getSize();
							new_process_reserving_os+=chossen_process.getSize();
							os_reserving+=chossen_process.getSize(); // update os reserving with the new process size
							find_process=true;
							
						}
						
					}
					
					
				}
			}
			
		}
	}
	
	
	// for UI
	
	// border pane for each step
	public BorderPane bp_steps() {
		BorderPane bp_start=new BorderPane();
		//#################################################################################
		// top
		
		HBox hb_top=new HBox();
		
			// left
			VBox vb_top_left=new VBox();
			
			HBox hb_t_top_left_top=new HBox();
			Text t_top_left_top=new Text("Job Queue");
			t_top_left_top.setStyle("-fx-font-size:15;-fx-font-weight:bold;");
			hb_t_top_left_top.getChildren().add(t_top_left_top);
			hb_t_top_left_top.setPadding(new Insets(0,139,10,148)); // top, right, bottom, left
			
			TableView<Pcb> tv_job_queue = update_job_queue_on_tableView(FXCollections.observableArrayList(q_jobs));
			
			
			vb_top_left.getChildren().addAll(hb_t_top_left_top,tv_job_queue);
			
			// right
			VBox vb_top_right=new VBox();
			
			HBox hb_t_top_right_top=new HBox();
			Text t_top_right_top=new Text("Ready Queue");
			t_top_right_top.setStyle("-fx-font-size:15;-fx-font-weight:bold;");
			hb_t_top_right_top.getChildren().add(t_top_right_top);
			hb_t_top_right_top.setPadding(new Insets(0,129,10,138)); // top, right, bottom, left
			
			TableView<Pcb> tv_ready_queue = update_ready_queue_on_tableView(FXCollections.observableArrayList(q_ready));
			
			vb_top_right.getChildren().addAll(hb_t_top_right_top,tv_ready_queue);
		
		hb_top.getChildren().addAll(vb_top_left,vb_top_right);
		hb_top.setSpacing(100);
		hb_top.setPadding(new Insets(10,0,0,350)); // top, right, bottom, left
		bp_start.setTop(hb_top);
		//#################################################################################
		// center
		
		VBox vb_center=new VBox();
		
			// center top
			VBox vb_center_top=new VBox();
			ScrollPane sp_center = new ScrollPane(vb_center_top);
			sp_center.setFitToWidth(true);
			sp_center.setFitToHeight(true);
			sp_center.setStyle("-fx-background-color: #7ac943;");
			sp_center.setPadding(new Insets(10,-100,0,0)); // top, right, bottom, left
			
			// memory dosent finish job
			if(!q_jobs.isEmpty() || !q_ready.isEmpty()) {
				vb_center_top.getChildren().add(hb_os(init_os_reserving));
			}
			
			for(int i=0;i<q_ready.size();i++) {	
				// first process on ready queue
				if(i==0) {
					
					// if process has previous hole
					if(q_ready.get(i).getBase_register()>init_os_reserving) {
						vb_center_top.getChildren().add(hb_hole(q_ready.get(i).getBase_register()-init_os_reserving));
					}
					
					vb_center_top.getChildren().add(hb_process(q_ready.get(i).getBase_register(), q_ready.get(i).getLimit_register(), q_ready.get(i).getProcess_id(), q_ready.get(i).getSize()));
					
					// if process has hole next of it
					if(q_ready.size()==1 && q_ready.get(i).getLimit_register()<memory_size) {
						vb_center_top.getChildren().add(hb_hole(memory_size-q_ready.get(i).getLimit_register()));
					}
					// if process has hole next of it (hole between two process)
					if(q_ready.size()>1 && q_ready.get(i).getLimit_register()<q_ready.get(i+1).getBase_register()) {
						vb_center_top.getChildren().add(hb_hole(q_ready.get(i+1).getBase_register()-q_ready.get(i).getLimit_register()));
					}
					
				}
				// middle process's on ready queue
				else if(q_ready.size()>1 && i>0 && i!=q_ready.size()-1) {
					// if process has hole next of it
					if(q_ready.get(i).getLimit_register()<q_ready.get(i+1).getBase_register()) {
						vb_center_top.getChildren().add(hb_process(q_ready.get(i).getBase_register(), q_ready.get(i).getLimit_register(), q_ready.get(i).getProcess_id(), q_ready.get(i).getSize()));
						vb_center_top.getChildren().add(hb_hole(q_ready.get(i+1).getBase_register()-q_ready.get(i).getLimit_register()));
					}
					else {
						vb_center_top.getChildren().add(hb_process(q_ready.get(i).getBase_register(), q_ready.get(i).getLimit_register(), q_ready.get(i).getProcess_id(), q_ready.get(i).getSize()));
					}
				}
				// final process on ready queue
				else if(q_ready.size()>1 && i==q_ready.size()-1) {
					// if process has hole next of it
					if(q_ready.get(i).getLimit_register()<memory_size) {
						vb_center_top.getChildren().add(hb_process(q_ready.get(i).getBase_register(), q_ready.get(i).getLimit_register(), q_ready.get(i).getProcess_id(), q_ready.get(i).getSize()));
						vb_center_top.getChildren().add(hb_hole(memory_size-q_ready.get(i).getLimit_register()));
					}
					else {
						vb_center_top.getChildren().add(hb_process(q_ready.get(i).getBase_register(), q_ready.get(i).getLimit_register(), q_ready.get(i).getProcess_id(), q_ready.get(i).getSize()));
					}
				}
				
			}
			
			
			vb_center_top.setPadding(new Insets(10,0,0,0)); // top, right, bottom, left // 353 350
			vb_center_top.setStyle("-fx-background-color: #7ac943;");
			
			if(vb_center_top.getChildren().size()==0) {
				HBox hb_center=new HBox();
				Text t_center=new Text("Memory finish all Processes");
				t_center.setStyle("-fx-font-size:18;-fx-font-weight:bold;");
				hb_center.getChildren().add(t_center);
				hb_center.setStyle("-fx-background-color:white;-fx-Border-color:black;-fx-Border-width:1;");
				hb_center.setPrefWidth(825);
				hb_center.setPrefHeight(400);
				hb_center.setAlignment(Pos.CENTER);
				
				vb_center_top.getChildren().add(hb_center);
				
				vb_center_top.setPadding(new Insets(10,100,0,350)); // top, right, bottom, left // 353 350
			}
			
			// center bottom
			VBox vb_bottom=new VBox();
			
			Text t_bottom_top=new Text("At time: "+time);
			t_bottom_top.setStyle("-fx-font-size:14;-fx-font-weight:bold;");
			
			Text t_bottom_center=new Text();
			t_bottom_center.setStyle("-fx-font-size:14;-fx-font-weight:bold;");
			String job_finished="-";
			if(al_job_finish.isEmpty()) {
				t_bottom_center.setText("job finished: "+job_finished);
			}
			else {
				t_bottom_center.setText("job finished: "+al_job_finish.toString());
			}
			
			
			Text t_bottom_bottom=new Text();
			t_bottom_bottom.setStyle("-fx-font-size:14;-fx-font-weight:bold;");
			String job_brought="-";
			if(al_job_brought_from_memory.isEmpty()) {
				t_bottom_bottom.setText("job brought into memory: "+job_brought);
			}
			else {
				t_bottom_bottom.setText("job brought into memory: "+al_job_brought_from_memory.toString());
			}
			
			vb_bottom.getChildren().addAll(t_bottom_top,t_bottom_center,t_bottom_bottom);
			vb_bottom.setPadding(new Insets(10,353,20,350)); // top, right, bottom, left
			vb_bottom.setSpacing(7);
			vb_bottom.setStyle("-fx-background-color: #7ac943;");
		vb_center.getChildren().addAll(sp_center,vb_bottom);
		bp_start.setCenter(vb_center);
		
		bp_start.setStyle("-fx-background-color:#7ac943;");
		return bp_start;
	}
	
	// update table view of job queue
 	@SuppressWarnings("unchecked")
	public TableView<Pcb> update_job_queue_on_tableView(ObservableList<Pcb> job_data) {
		TableView<Pcb> tv_job_queue = new TableView<>();
		tv_job_queue.setPrefWidth(362);
		tv_job_queue.setPrefHeight(194.5);
		//tv_job_queue.setStyle("-fx-border-color:black; -fx-border-width:1;");
		// process id column
		TableColumn<Pcb, String> processIdCol = new TableColumn<Pcb, String>("process id");
		processIdCol.setMinWidth(120);
		processIdCol.setCellValueFactory(new PropertyValueFactory<Pcb, String>("process_id"));
		processIdCol.setStyle("-fx-alignment: CENTER;");
		
		// process size
		TableColumn<Pcb, String> processSizeCol = new TableColumn<Pcb, String>("memory needed");
		processSizeCol.setMinWidth(120);
		processSizeCol.setCellValueFactory(new PropertyValueFactory<Pcb, String>("size"));
		processSizeCol.setStyle("-fx-alignment: CENTER;");
		
		// process time in memory
		TableColumn<Pcb, String> processTimeCol = new TableColumn<Pcb, String>("time in memory");
		processTimeCol.setMinWidth(120);
		processTimeCol.setCellValueFactory(new PropertyValueFactory<Pcb, String>("time_in_memory"));
		processTimeCol.setStyle("-fx-alignment: CENTER;");
	
		tv_job_queue.setItems(job_data);
		tv_job_queue.getColumns().addAll(processIdCol, processSizeCol, processTimeCol);
		
		return tv_job_queue;
	}
	
 	// update table view of ready queue
 	@SuppressWarnings("unchecked")
	public TableView<Pcb> update_ready_queue_on_tableView(ObservableList<Pcb> ready_data) {
		TableView<Pcb> tv_ready_queue = new TableView<>();
		tv_ready_queue.setPrefWidth(362);
		tv_ready_queue.setPrefHeight(194.5);
		
		// process id column
		TableColumn<Pcb, String> processIdCol = new TableColumn<Pcb, String>("process id");
		processIdCol.setMinWidth(120);
		processIdCol.setCellValueFactory(new PropertyValueFactory<Pcb, String>("process_id"));
		processIdCol.setStyle("-fx-alignment: CENTER;");
		
		// process size
		TableColumn<Pcb, String> processSizeCol = new TableColumn<Pcb, String>("memory needed");
		processSizeCol.setMinWidth(120);
		processSizeCol.setCellValueFactory(new PropertyValueFactory<Pcb, String>("size"));
		processSizeCol.setStyle("-fx-alignment: CENTER;");
		
		// process time in memory
		TableColumn<Pcb, String> processTimeCol = new TableColumn<Pcb, String>("time in memory");
		processTimeCol.setMinWidth(120);
		processTimeCol.setCellValueFactory(new PropertyValueFactory<Pcb, String>("time_in_memory"));
		processTimeCol.setStyle("-fx-alignment: CENTER;");
	
		tv_ready_queue.setItems(ready_data);
		tv_ready_queue.getColumns().addAll(processIdCol, processSizeCol, processTimeCol);
		
		return tv_ready_queue;
	}
			
 	// os model (hbox)
 	public HBox hb_os(int size) {
 		HBox hb_center=new HBox();
		
		// left
		HBox hb_center_left=new HBox();
		Text t_center_left=new Text("OS"+" ("+size+" MB)");
		t_center_left.setStyle("-fx-font-size:18;-fx-font-weight:bold;");
		hb_center_left.getChildren().add(t_center_left);
		hb_center_left.setStyle("-fx-background-color:#00BAA4;-fx-Border-color:black;-fx-Border-width:1;");
		hb_center_left.setPrefWidth(825);
		hb_center_left.setPrefHeight(60);
		hb_center_left.setAlignment(Pos.CENTER);
		
		// right
		VBox vb_center_right=new VBox();
			
			//right top
			HBox hb_center_right_top=new HBox();
			Text t_center_right_top=new Text("Base: 0");
			t_center_right_top.setStyle("-fx-font-size:12;-fx-font-weight:bold;");
			hb_center_right_top.getChildren().add(t_center_right_top);
			// right bottom
			HBox hb_center_right_bottom=new HBox();
			Text t_center_right_bottom=new Text("Limit: "+size);
			t_center_right_bottom.setStyle("-fx-font-size:12;-fx-font-weight:bold;");
			hb_center_right_bottom.getChildren().add(t_center_right_bottom);
			
		vb_center_right.getChildren().addAll(hb_center_right_top,hb_center_right_bottom);
		vb_center_right.setSpacing(25);
		vb_center_right.setPadding(new Insets(0,0,0,3)); // top, right, bottom, left
		
		hb_center.getChildren().addAll(hb_center_left,vb_center_right);	
		hb_center.setPadding(new Insets(0,0,0,350)); // top, right, bottom, left
		return hb_center;
	}
 	
 	// process model (hbox)
	public HBox hb_process(int base,int limit,int process_id,int size) {
		HBox hb_center=new HBox();
		
		// left
		HBox hb_center_left=new HBox();
		Text t_center_left=new Text("P"+process_id+" ("+size+" MB)");
		t_center_left.setStyle("-fx-font-size:18;-fx-font-weight:bold;");
		hb_center_left.getChildren().add(t_center_left);
		hb_center_left.setStyle("-fx-background-color:white;-fx-Border-color:black;-fx-Border-width:1;");
		hb_center_left.setPrefWidth(825);
		hb_center_left.setPrefHeight(60);
		hb_center_left.setAlignment(Pos.CENTER);
		
		// right
		VBox vb_center_right=new VBox();
			
			//right top
			HBox hb_center_right_top=new HBox();
			Text t_center_right_top=new Text("Base: "+base);
			t_center_right_top.setStyle("-fx-font-size:12;-fx-font-weight:bold;");
			hb_center_right_top.getChildren().add(t_center_right_top);
			// right bottom
			HBox hb_center_right_bottom=new HBox();
			Text t_center_right_bottom=new Text("Limit: "+limit);
			t_center_right_bottom.setStyle("-fx-font-size:12;-fx-font-weight:bold;");
			hb_center_right_bottom.getChildren().add(t_center_right_bottom);
			
		vb_center_right.getChildren().addAll(hb_center_right_top,hb_center_right_bottom);
		vb_center_right.setSpacing(25);
		vb_center_right.setPadding(new Insets(0,0,0,3)); // top, right, bottom, left
		
		hb_center.getChildren().addAll(hb_center_left,vb_center_right);	
		hb_center.setPadding(new Insets(0,0,0,350)); // top, right, bottom, left
		return hb_center;
	}
	
	// hole model (hbox)
	public HBox hb_hole(int size) {
		HBox hb=new HBox();
			HBox hb_center=new HBox();
			Text t_center=new Text("Hole"+" ("+size+" MB)");
			t_center.setStyle("-fx-font-size:18;-fx-font-weight:bold;");
			hb_center.getChildren().add(t_center);
			hb_center.setStyle("-fx-background-color:red;-fx-Border-color:black;-fx-Border-width:1;");
			hb_center.setPrefWidth(825);
			hb_center.setPrefHeight(60);
			hb_center.setAlignment(Pos.CENTER);
		hb.getChildren().add(hb_center);
		hb.setPadding(new Insets(0,0,0,350)); // top, right, bottom, left
		return hb;
	}
		
		
	
	public static void main(String[] args) {
		launch(args);
	}
}
