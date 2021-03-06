<?xml version="1.0" encoding="UTF-8"?>
<StyledLayerDescriptor version="1.1.0" xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd" xmlns="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<NamedLayer>
 <se:Name>TN.CommonTransportElements.TransportArea</se:Name>
 <UserStyle>
  <se:Name> TN.CommonTransportElements.TransportArea.Default</se:Name>
  <IsDefault>1</IsDefault>
  <se:FeatureTypeStyle version="1.1.0">
   <se:Description>
    <se:Title>Generic Area Default Style</se:Title>
    <se:Abstract>The geometry is rendered using a grey (#A9A9A9) fill and a solid black (#000000) outline with a stroke width of 1 pixel.</se:Abstract>
   </se:Description>
   <se:FeatureTypeName>Network:Area</se:FeatureTypeName>
   <se:Rule>
    <se:PolygonSymbolizer>
     <se:Geometry>
      <ogc:PropertyName>Network:geometry</ogc:PropertyName>
     </se:Geometry>
     <se:Fill/>
     <se:Stroke/>
    </se:PolygonSymbolizer>
   </se:Rule>
  </se:FeatureTypeStyle>
 </UserStyle>
</NamedLayer>
</StyledLayerDescriptor>
