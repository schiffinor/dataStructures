import java.awt.*;

public class JHolder extends Component {

    public Landscape landscapeHolder;

    public JHolder(Landscape landscape){
        landscapeHolder = landscape;
    }
    @Override
    public boolean equals(Object obj) {
        return this.landscapeHolder.equals(obj);
    }
}
