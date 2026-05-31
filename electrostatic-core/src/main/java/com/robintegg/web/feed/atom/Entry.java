package com.robintegg.web.feed.atom;

import com.robintegg.web.utils.OffsetDateTimeXmlAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Entry {

    private Title title;
    private List<Link> link;
    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    private OffsetDateTime published;
    @XmlJavaTypeAdapter(OffsetDateTimeXmlAdapter.class)
    private OffsetDateTime updated;
    private String id;
    private Content content;
    private List<Author> author;
    private List<Category> category;
    private Summary summary;
    @XmlElement(name = "thumbnail", namespace = AtomConstants.YAHOO_MEDIA_NAMESPACE)
    private MediaThumbnail mediaThumbnail;
    @XmlElement(name = "content", namespace = AtomConstants.YAHOO_MEDIA_NAMESPACE)
    private MediaContent mediaContent;

}
