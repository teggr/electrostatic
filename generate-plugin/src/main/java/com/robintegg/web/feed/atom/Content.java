package com.robintegg.web.feed.atom;

import com.robintegg.web.utils.CDataXmlAdatper;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Content {

    @XmlAttribute
    private String type;
    @XmlAttribute(name = "xml:base")
    private String xmlBase;
    @XmlValue
    @XmlJavaTypeAdapter(CDataXmlAdatper.class)
    private String value;

}
