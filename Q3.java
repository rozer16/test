dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
dbFactory.setXIncludeAware(false);
dbFactory.setExpandEntityReferences(false);
