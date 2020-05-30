package cn.sms.service.listener;

import cn.sms.service.Utils.SendMsg;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserListener {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.sms.queue",durable = "true"),
            exchange = @Exchange(value = "leyou.sms.exchange",ignoreDeclarationExceptions = "true"),
            key = "sms.verify.code"
    ))
    public void sendCode(Map<String,String> msg){
        if (msg == null || msg.size()<0){
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");
        if (phone==null ||code ==null){
            return;
        }

        SendMsg.send(phone,code);

    }
}
