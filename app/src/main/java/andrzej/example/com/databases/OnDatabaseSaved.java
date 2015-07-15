package andrzej.example.com.databases;

/**
 * Created by andrzej on 15.07.15.
 */
public interface OnDatabaseSaved {
    void onSucess();
    void onRecordAlreadyExsists();
}
