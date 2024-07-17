package A_RunApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import B_Classes.Pcb;

public class Driver_Test {
	
	// for data
	static List<Pcb> q_jobs=new ArrayList<>();
	static List<Pcb> q_ready=new ArrayList<>();
	static List<Integer> al_time_of_process=new ArrayList<>();
	static int memory_size=2048;
	static int init_os_reserving=512;
	static int os_reserving=init_os_reserving;
	static int num_of_holes=0;
	static int num_new_process=0;
	
	
	public static void main(String[] args) {
		read_job_file(new File("src\\job.txt"));
		read_ready_file(new File("src\\ready.txt"));
		
		if(q_ready.isEmpty() && !q_jobs.isEmpty()) {
			// to set process on ready queue from job queue
			for(int i=0;i<q_jobs.size();i++) {
				
				if(os_reserving+q_jobs.get(i).getSize()<=memory_size) { // if (os reserving from memory + the job size on job queue) less or equal (memory size)
					Pcb chossen_process=q_jobs.get(i); // store this process on chossen_process
					
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
				System.out.println("The memory size cannot handel the processes size");
				System.exit(0);
			}
			else {
				// sort the time of process
				Collections.sort(al_time_of_process);
			}
		}
		

		// while there is a processes inside queue ready or queue job
		while(!q_ready.isEmpty() || !q_jobs.isEmpty()) {
			
			// calculate number of holes
			calc_num_of_holes();
			
			System.out.println(q_ready.toString());
			System.out.println("num of holes: "+num_of_holes);
			System.out.println(al_time_of_process.toString());
			System.out.println("============================================\n\n");
			
			
			// if number of holes be greater than 3 --> make compaction (re-allocate to jobs/process that inside ready queue (q_ready))
			if(num_of_holes>3) {
				compactions();
				
				// to set process on ready queue from job queue
				for(int i=0;i<q_jobs.size();i++) {
					
					if(os_reserving+q_jobs.get(i).getSize()<=memory_size) { // if (os reserving from memory + the job size on job queue) less or equal (memory size)
						Pcb chossen_process=q_jobs.get(i); // store this process on chossen_process
						
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
		
		// calculate number of holes
		calc_num_of_holes();
		
		System.out.println("ready queue: "+q_ready.toString());
		System.out.println("job queue: "+q_jobs.toString());
		System.out.println("num of holes: "+num_of_holes+"\n");
		
		System.out.println("--- "+"Memory is empty :)"+" ---");

	}

	
	//////////////////////////////////////////////////////////////////////////////////
	// Methods:
	
	//-- Read Jobs from job.txt and set it's process(jobs) inside job queue (q_jobs)
	public static void read_job_file(File file) {
		
		// check if the file exist
		if(!file.exists()) {
			System.out.println("there isn't job file !!!");
		}
		else {
			
			try {
				// get data thats inside the file.
				Scanner in = new Scanner(file);
				String s_insideLine[] = null;
				
				for(int i=0;in.hasNextLine();i++) {
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
	public static void read_ready_file(File file) {
			
			// check if the file exist
			if(!file.exists()) {
				System.out.println("there isn't ready file !!!");
			}
			else {
				
				try {
					// get data thats inside the file.
					Scanner in = new Scanner(file);
					String s_insideLine[] = null;
					
					for(int i=0;in.hasNextLine();i++) {
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
	public static void compactions() {
		
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
	public static void calc_num_of_holes() {
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
	public static void first_fit() {
		
		for(int i=0;i<q_ready.size();i++) {
			// if the (time in memory) of process on ready queue equal 0 --> (the process is executed/finished)
			if(q_ready.get(i).getTime_in_memory()==0) {
				int hole_size=q_ready.get(i).getSize(); // set hole size as the process that executed/finished size
				
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
	

}
