package storage;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PkStorage { // 로그인했을 때, 현재 pk 저장하는 저장소
    private static PkStorage instance;
    private int curPk;
    private String name;
    private String ip;
    public PkStorage(){
        this.curPk = -1;
    }

    public static synchronized PkStorage getInstance(){
        if(instance == null)
            instance = new PkStorage();
        return instance;
    }
}
