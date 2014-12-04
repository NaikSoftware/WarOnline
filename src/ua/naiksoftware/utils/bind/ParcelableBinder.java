package ua.naiksoftware.utils.bind;

import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Naik on 01.12.14.
 */
public class ParcelableBinder<T> implements Parcelable {

    private T obj;
    private BindManager<T> bindManager;

    public static final Parcelable.Creator<ParcelableBinder> CREATOR = new Parcelable.Creator<ParcelableBinder>() {

        @Override
        public ParcelableBinder createFromParcel(Parcel in) {
            BindManager bm = (BindManager) in.readStrongBinder();
            return new ParcelableBinder(bm.get());
        }

        @Override
        public ParcelableBinder[] newArray(int size) {
            return new ParcelableBinder[0];
        }
    };

    public ParcelableBinder(T obj) {
        this.obj = obj;
        this.bindManager = new BindManager(obj);
    }

    public T getObj() {
        return  obj;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(bindManager);
    }

    private static class BindManager<T> extends Binder {
        private T obj;
        public BindManager(T obj) {
            super();
            this.obj = obj;
        }
        public T get() {
            return  obj;
        }
    }
}
