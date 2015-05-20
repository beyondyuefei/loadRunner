# loadRunner
a simple loadrunner for concurrency test

demo:

//10个并发、跑5轮
 SimpleLoadRunner.loadRunner(10,5, new Runnable() {    
  @Override    
    public void run() {    
         //your service method, like xxxService.execute();    
     }      
 });
