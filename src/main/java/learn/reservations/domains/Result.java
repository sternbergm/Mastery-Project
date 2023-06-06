package learn.reservations.domains;

import java.util.ArrayList;
import java.util.List;

public class Result<Obj>{
    List<String> messages;
    Obj obj;

    public Result() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public List<String> getMessages(){
        return messages;
    }

    public Obj getPayload() {
        return obj;
    }

    public void setPayload(Obj obj) {
        this.obj = obj;
    }

    public boolean isSuccessful(){
        return messages.size()==0;
    }
}
