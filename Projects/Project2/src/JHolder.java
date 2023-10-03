import java.awt.*;

/**
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
 */
public class JHolder extends Component {

    /**
     * This class is used to hold a Landscape object. I never ended up using it though.
     */
    public final Landscape landscapeHolder;

    public JHolder(Landscape landscape){
        landscapeHolder = landscape;
    }
    @Override
    public boolean equals(Object obj) {
        return this.landscapeHolder.equals(obj);
    }
}
