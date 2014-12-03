package dawei.project.aggregator;

public class Util {

	public static void printArray(String n, Object a[]){
		System.out.print(n+ ": ");
		for(Object i:a)
			System.out.print(i+" ");
		System.out.println();
	}
	public static void printArray(String n, int a[]){
		System.out.print(n+ ": ");
		for(int i:a)
			System.out.print(i+" ");
		System.out.println();
	}	
	public static void printArray(byte a[]){
		for(byte i:a){
			System.out.print(i+" ");
		}
		System.out.println();
	}
	public static void printArray(String n, byte a[]){
		System.out.print(n+ ": ");
		for(byte i:a){
			System.out.print(i+" ");
		}
		System.out.println();
	}
	public static void printArray(String a[]){
		for(String i:a){
			System.out.print(i+" ");
		}
		System.out.println();
	}
	public static void printArray(int a[]){
		for(int i:a){
			System.out.print(i+" ");
		}
		System.out.println();
	}


}
