package com.robintegg.web.feed.atom;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Generator {

    @XmlAttribute
    private String uri;
    @XmlAttribute
    private String version;
    @XmlValue
    private String value;

}
