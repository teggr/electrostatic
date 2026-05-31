package com.robintegg.web.feed.atom;

import com.robintegg.web.utils.OffsetDateTimeXmlAdapter;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.XMLConstants;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "feed")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomFeed {

    private Generator generator;
    private List<Link> link;
    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    private OffsetDateTime updated;
    private String id;
    private Title title;
    private String subtitle;
    private List<Author> author;
    private List<Entry> entry;

}
