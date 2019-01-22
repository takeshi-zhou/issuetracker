

import java.util.Iterator;


public class OpenMapRealVector
    {

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OpenMapRealVector)) {
                return false;
            }
            OpenMapRealVector other = (OpenMapRealVector) obj;
            if (virtualSize != other.virtualSize) {
                return false;
            }
            if (Double.doubleToLongBits(epsilon) !=
                    Double.doubleToLongBits(other.epsilon)) {
                return false;
            }
            Iterator iter = entries.iterator();
            while (iter.hasNext()) {
                iter.advance();
                double test = other.getEntry(iter.key());
                if (Double.doubleToLongBits(test) != Double.doubleToLongBits(iter.value())) {
                    return false;
                }
            }
            iter = other.getEntries().iterator();
            while (iter.hasNext()) {
                iter.advance();
                double test = iter.value();
                if (Double.doubleToLongBits(test) != Double.doubleToLongBits(getEntry(iter.key()))) {
                    return false;
                }
            }
            return true;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((bio == null) ? 0 : bio.hashCode());
            result = prime * result + ((email == null) ? 0 : email.hashCode());
            result = prime * result + ((username == null) ? 0 : username.hashCode());
            result = prime * result + ((website == null) ? 0 : website.hashCode());
            return result;
        }

}
