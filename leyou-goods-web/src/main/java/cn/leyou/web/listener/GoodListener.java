package cn.leyou.web.listener;


import cn.leyou.web.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoodListener {

    @Autowired
    private GoodsHtmlService goodsHtmlService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.web.queue",declare = "true"),
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
        this.goodsHtmlService.createHtml(id);
    }


    @RabbitListener(bindings=@QueueBinding(
            value = @Queue(value = "leyou.delete.web.queue",declare = "true"),
            exchange = @Exchange(value = "leyou.item.exchange",
            ignoreDeclarationExceptions = "true",
            type = ExchangeTypes.FANOUT),
            key = "item.delete"
    ))
    public void listenerDelete(Long id) throws IOException {
       if (id==null){
           return;
       }
       this.goodsHtmlService.deleteHtml(id);
   }

}
