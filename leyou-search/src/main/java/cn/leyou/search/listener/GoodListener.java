/*
package cn.leyou.search.listener;

import cn.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.Bindings;
import java.io.IOException;

@Component
public class GoodListener {

    @Autowired
    private SearchService searchService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.index.queue",declare = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
            ignoreDeclarationExceptions = "true",
            type = ExchangeTypes.FANOUT),
            key="item.insert"
    ))
    public void listenerCreate(Long id) throws IOException {
        if (id==null){
            return;
        }
        this.searchService.createIndex(id);
    }


    @RabbitListener(bindings=@QueueBinding(
            value = @Queue(value = "leyou.delete.index.queue",declare = "true"),
            exchange = @Exchange(value = "leyou.item.exchange",
            ignoreDeclarationExceptions = "true",
            type = ExchangeTypes.FANOUT),
            key = "item.delete"
    ))
    public void listenerDelete(Long id) throws IOException {
       if (id==null){
           return;
       }
       this.searchService.delectIndex(id);
   }

}
*/
