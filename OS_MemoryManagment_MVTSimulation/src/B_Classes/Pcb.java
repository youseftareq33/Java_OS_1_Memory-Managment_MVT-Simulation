package B_Classes;

public class Pcb {
	
	//=== Attributes --------------------------------------------------------
	private int process_id;
	private int size;
	private int time_in_memory;
	private int base_register;
	private int limit_register;
	
	
	//=== Constructor --------------------------------------------------------
	public Pcb(int process_id, int size, int time_in_memory, int base_register, int limit_register) {
		this.process_id = process_id;
		this.size = size;
		this.time_in_memory = time_in_memory;
		this.base_register = base_register;
		this.limit_register = limit_register;
	}

	
	//=== Getter and Setter --------------------------------------------------
	public int getProcess_id() {
		return process_id;
	}
	public void setProcess_id(int process_id) {
		this.process_id = process_id;
	}
	/////////////////////////////////////
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	/////////////////////////////////////
	public int getTime_in_memory() {
		return time_in_memory;
	}
	public void setTime_in_memory(int time_in_memory) {
		this.time_in_memory = time_in_memory;
	}
	/////////////////////////////////////
	public int getBase_register() {
		return base_register;
	}
	public void setBase_register(int base_register) {
		this.base_register = base_register;
	}
	/////////////////////////////////////
	public int getLimit_register() {
		return limit_register;
	}
	public void setLimit_register(int limit_register) {
		this.limit_register = limit_register;
	}

	
	// toString
	@Override
	public String toString() {
		
		return "Pcb "+process_id+":"+"\n"+
			   "process_id: "+process_id+"\n"+
			   "size: "+size+"\n"+
			   "time_in_memory: "+time_in_memory+"\n"+
			   "base_register: "+base_register+"\n"+
			   "limit_register: "+limit_register+"\n"+
			   "---------------------------------------"+"\n";
	}
	
}
