import java.util.Vector;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


public class ConsumerProducer extends Thread {
	String info;
	static int length;
	int consumenum;
	static int producenum;
	static int emptyspace;
	static private 	Vector aVector = new Vector();
	static int count=0;
	static int count1=0;
	static int MAX=3;
	static int MIN=1;
	Semaphore sem;
	public ConsumerProducer() {
		
	}
	
	public ConsumerProducer(Semaphore sem,String info,int length,int consumenum,int producenum) {
		this.sem=sem;
		this.info=info;
		this.length=length;
		this.consumenum=consumenum;
		this.producenum=producenum;
	}
	
	public void isProtected2() {
		
		
		
			try {
				sem.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			Random random=new Random();
			int randNumber =random.nextInt(MAX - MIN + 1) + MIN;
			
			if(randNumber==1) {
				this.consumenum=3;
				count1+=1;
				System.out.println("count1:"+count1);
				//if(count1==500) System.exit(1);
			
			
				while(length-emptyspace>=this.consumenum) {
						System.out.println("consume "+this.consumenum+" items");
						emptyspace+=this.consumenum;
						if( emptyspace>=producenum ) {
							break;
						}
				}
				sem.release();
				System.out.println("-----------------" + sem.availablePermits());
			}
				
			
			else if(randNumber==2) {
				this.consumenum=5;
				count1+=1;
				System.out.println("count1:"+count1);
				//if(count1==500) System.exit(1);
				
			
				while(length-emptyspace>=this.consumenum) {
						System.out.println("consume "+this.consumenum+" items");
						emptyspace+=this.consumenum;
						if( emptyspace>=producenum ) {
							break;
						}
				}
				sem.release();
				System.out.println("-----------------" + sem.availablePermits());
			}
			
			
			
			
			else {
				this.consumenum=2;
				count1+=1;
				System.out.println("count1:"+count1);
				//if(count1==500) System.exit(1);
				
				while(length-emptyspace>=this.consumenum) {
						System.out.println("consume "+this.consumenum+" items");
						emptyspace+=this.consumenum;
						if( emptyspace>=producenum ) {
							break;
						}
				}
				sem.release();
				System.out.println("-----------------" + sem.availablePermits());
			}
			
		
	}
	

	public void isProtected1() {
			
				try {
					sem.acquire();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
				count+=1;
				System.out.println("count:"+count);
				//if(count==500) System.exit(1);
				
				
				while(emptyspace>=producenum) {
						System.out.println("produce "+producenum+" items");
						emptyspace-=producenum;
						if(length-emptyspace>=2) {
							break;
						}
				}
					sem.release();
					//System.out.println("-----------------" + semp.availablePermits());
	}
	


	public void run() {
		if(this.info.equals("producer")) {
			isProtected1();
		}else {
			isProtected2();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Vector aVector = new Vector();
		length=Integer.parseInt(args[0]);
		emptyspace=Integer.parseInt(args[1]);
		int c=Integer.parseInt(args[2]);
		int p=Integer.parseInt(args[3]);
		
		Semaphore semp = new Semaphore(1);//只能1个线程同时访问
	
		
		ConsumerProducer addc[]=new ConsumerProducer[length];
		ConsumerProducer addp[]=new ConsumerProducer[length];
		for(int index=0;index<length;index++) {
			addc[index]=new ConsumerProducer(semp,"consumer",length,c,p);
			addp[index]=new ConsumerProducer(semp,"producer",length,c,p);
			addc[index].start();
			addp[index].start();
		}
	}
}
