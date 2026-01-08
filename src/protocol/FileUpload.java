package protocol;

public class FileUpload {

    private final String filename;
    private final byte[] data;
    private final String senderId;

    public FileUpload(String filename, byte[] data, String senderId) {
        this.filename = filename;
        this.data = data;
        this.senderId = senderId;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public String getSenderId() {
        return senderId;
    }
}
