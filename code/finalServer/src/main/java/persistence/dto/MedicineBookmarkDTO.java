package persistence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class MedicineBookmarkDTO implements Serializable {

    private int pk;
    private int user_pk;
    private int infirmary_pk;
    private int medicine_pk;

    public MedicineBookmarkDTO(int pk, int user_pk, int infirmary_pk, int medicine_pk)
    {
        this.pk = pk;
        this.user_pk = user_pk;
        this.infirmary_pk = infirmary_pk;
        this.medicine_pk = medicine_pk;
    }
    public byte[] getBytes() throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeInt(user_pk);
        dos.writeInt(infirmary_pk);
        dos.writeInt(medicine_pk);

        return buf.toByteArray();
    }
}
