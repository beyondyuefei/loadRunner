# loadRunner
简单的性能测试，打出性能指标： tps、 average process time 等

demo:

//10个并发、跑5轮     
 SimpleLoadRunner.loadRunner(10,5, new Runnable() {    
  @Override    
    public void run() {    
         //your service method, like xxxService.execute();    
     }      
 });
