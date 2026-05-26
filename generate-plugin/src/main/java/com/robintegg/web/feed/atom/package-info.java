@XmlSchema(
    namespace = AtomConstants.ATOM_FEED_NAMESPACE,
    elementFormDefault = XmlNsForm.QUALIFIED,
    xmlns = {
        @XmlNs(prefix = XMLConstants.DEFAULT_NS_PREFIX, namespaceURI = AtomConstants.ATOM_FEED_NAMESPACE),
        @XmlNs(prefix = AtomConstants.YAHOO_MEDIA_PREFIX, namespaceURI = AtomConstants.YAHOO_MEDIA_NAMESPACE)
    }
)
package com.robintegg.web.feed.atom;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;

import javax.xml.XMLConstants;