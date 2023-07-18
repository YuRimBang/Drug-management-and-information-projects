package persistence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class InfirmaryMedicineDTO {
    private int pk;
    private int infirmary_pk;
    private int medicine_stock;
    private int medicine_pk;
    private String medicine_name;

    public InfirmaryMedicineDTO(int pk, int infirmary_pk, int medicine_stock, int medicine_pk)
    {
        this.pk = pk;
        this.infirmary_pk = infirmary_pk;
        this.medicine_stock = medicine_stock;
        this.medicine_pk = medicine_pk;
    }
    public InfirmaryMedicineDTO(int pk, int infirmary_pk, int medicine_stock, int medicine_pk, String medicine_name)
    {
        this.pk = pk;
        this.infirmary_pk = infirmary_pk;
        this.medicine_stock = medicine_stock;
        this.medicine_pk = medicine_pk;
        this.medicine_name = medicine_name;
    }

    public static InfirmaryMedicineDTO readInfirmaryMedicine(DataInputStream inputStream) throws IOException {
        int pk = inputStream.readInt();
        int infirmary_pk = inputStream.readInt();
        int medicine_stock = inputStream.readInt();
        int medicine_pk = inputStream.readInt();
        return new InfirmaryMedicineDTO(pk,infirmary_pk, medicine_stock,medicine_pk);

    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buf);

        dos.writeInt(pk);
        dos.writeInt(infirmary_pk);
        dos.writeInt(medicine_stock);
        dos.writeInt(medicine_pk);
        return buf.toByteArray();
    }
    public String toString() {
        return
                "품목명: " + medicine_name
                        + "\n재고: " + medicine_stock;
    }
}
