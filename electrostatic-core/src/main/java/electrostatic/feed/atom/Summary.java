package electrostatic.feed.atom;

import electrostatic.utils.CDataXmlAdatper;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Summary {

    @XmlAttribute
    private String type;
    @XmlValue
    @XmlJavaTypeAdapter(CDataXmlAdatper.class)
    private String value;

}
