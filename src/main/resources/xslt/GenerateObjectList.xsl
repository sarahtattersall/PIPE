<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <!-- Changed 24 Nov 2006 by David Patterson 
      1.  When run with Java 5.0's default parser, errors are raised because 
      attribute definitions are not the first in element definitions for the 
      match=place, match=arc and name=place-transition templates. 
      They have now been placed correctly.
   -->
  
   <xsl:output method="xml" encoding="iso-8859-1" omit-xml-declaration="no" indent="yes"/>
   
   <xsl:strip-space elements="*"/>
   
   <xsl:template match="pnml">	
      <xsl:element name="pntools">
         <xsl:attribute name="id">
            <xsl:value-of select="net/@id"/>
         </xsl:attribute>	
         <xsl:attribute name="type">
            <xsl:value-of select="net/@type"/>
         </xsl:attribute>		
         <xsl:apply-templates select="net"/>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="net">
   	  <xsl:apply-templates select="token"/>
      <xsl:apply-templates select="labels"/>
      <xsl:apply-templates select="definition"/>
      <xsl:apply-templates select="place"/>
      <xsl:apply-templates select="transition"/>
      <xsl:apply-templates select="arc"/>
      <xsl:apply-templates select="stategroup"/>
      <xsl:apply-templates select="page"/>
      <xsl:apply-templates select="module"/>
   </xsl:template>
   
   <xsl:template match="page">
   	  <xsl:apply-templates select="token"/>
      <xsl:apply-templates select="labels"/>
      <xsl:apply-templates select="definition"/>
      <xsl:apply-templates select="place"/>
      <xsl:apply-templates select="transition"/>
      <xsl:apply-templates select="arc"/>
      <xsl:apply-templates select="module"/>
      <xsl:apply-templates select="stategroup"/>
   </xsl:template>
   
   <xsl:template match="module">
   	  <xsl:apply-templates select="token"/>
      <xsl:apply-templates select="labels"/>
      <xsl:apply-templates select="definition"/>
      <xsl:apply-templates select="place"/>
      <xsl:apply-templates select="transition"/>
      <xsl:apply-templates select="arc"/>
      <xsl:apply-templates select="interface"/>
      <xsl:apply-templates select="stategroup"/>
   </xsl:template>
   
	<xsl:template match="token">
		<xsl:element name="token">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="enabled">
				<xsl:value-of select="@enabled" />
			</xsl:attribute> 
			<xsl:attribute name="red">
				<xsl:value-of select="@red" />
			</xsl:attribute>
			<xsl:attribute name="green">
				<xsl:value-of select="@green" />
			</xsl:attribute>
			<xsl:attribute name="blue">
				<xsl:value-of select="@blue" />
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
   <xsl:template match="labels">
      <xsl:element name="labels">
         <xsl:attribute name="xPosition">
            <xsl:value-of select="@x"/>
         </xsl:attribute>
         <xsl:attribute name="yPosition">
            <xsl:value-of select="@y"/>
         </xsl:attribute>
         <xsl:attribute name="w">
            <xsl:value-of select="@width"/>
         </xsl:attribute>
         <xsl:attribute name="h">
            <xsl:value-of select="@height"/>
         </xsl:attribute>
         <xsl:attribute name="border">
            <xsl:value-of select="@border"/>
         </xsl:attribute>
         <xsl:attribute name="txt">
            <xsl:value-of select="text"/>
         </xsl:attribute>                        
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="definition">
      <xsl:element name="definition">
         <xsl:attribute name="positionX">
            <xsl:value-of select="graphics/position/@x"/>
         </xsl:attribute>
         <xsl:attribute name="positionY">
            <xsl:value-of select="graphics/position/@y"/>
         </xsl:attribute>         
         <xsl:attribute name="expression">
            <xsl:value-of select="@expression"/>
         </xsl:attribute>
         <xsl:attribute name="name">
            <xsl:value-of select="@name"/>
         </xsl:attribute>
         <xsl:attribute name="type">
            <xsl:value-of select="@defType"/>
         </xsl:attribute>
      </xsl:element>
   </xsl:template>   
   
   <xsl:template match="place">
      <xsl:element name="place">
         <!-- David Patterson 24 Nov 2006 Move call-template after attributes -->
         <xsl:attribute name="initialMarking">
            <xsl:value-of select="initialMarking/value"/>
         </xsl:attribute>
         <xsl:attribute name="markingOffsetX">
            <xsl:value-of select="initialMarking/graphics/offset/@x"/>
         </xsl:attribute>
         <xsl:attribute name="markingOffsetY">
            <xsl:value-of select="initialMarking/graphics/offset/@y"/>
         </xsl:attribute>
         <xsl:attribute name="capacity">
            <xsl:value-of select="capacity/value"/>
         </xsl:attribute>
<!--         <xsl:apply-templates select="place-toolspecific"/>         -->
<!--         <xsl:attribute name="parameter">
            <xsl:value-of select="toolspecific/name/value"/>
         </xsl:attribute>-->
         <xsl:attribute name="parameter">
            <xsl:value-of select="toolspecific/@markingDefinition"/>
         </xsl:attribute>         
         <xsl:call-template name="place-transition"/>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="transition">
      <xsl:element name="transition">
         <xsl:attribute name="rate">
            <xsl:value-of select="rate/value"/>
         </xsl:attribute>
         <xsl:attribute name="timed">
            <xsl:value-of select="timed/value"/>
         </xsl:attribute>
         <xsl:attribute name="angle">
            <xsl:value-of select="orientation/value"/>
         </xsl:attribute>
         <xsl:attribute name="infiniteServer">
            <xsl:value-of select="infiniteServer/value"/>
         </xsl:attribute>
         <xsl:attribute name="priority">
            <xsl:value-of select="priority/value"/>
         </xsl:attribute>
         <xsl:attribute name="weight">
            <xsl:value-of select="weight/value"/>
         </xsl:attribute>
<!--         <xsl:attribute name="parameter">
            <xsl:value-of select="toolspecific/name/value"/>
         </xsl:attribute>               -->
         <xsl:attribute name="parameter">
            <xsl:value-of select="toolspecific/@rateDefinition"/>
         </xsl:attribute>          
         <xsl:call-template name="place-transition"/>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="arc">
      <xsl:element name="arc">
         <!-- David Patterson 24 Nov 2006 Move call-template after attributes -->
         <xsl:attribute name="source">
            <xsl:value-of select="@source"/>
         </xsl:attribute>
         <xsl:attribute name="target">
            <xsl:value-of select="@target"/>
         </xsl:attribute>
         <xsl:attribute name="inscription">
            <xsl:value-of select="inscription/value"/>
         </xsl:attribute>
         <xsl:attribute name="inscriptionOffsetX">
            <xsl:value-of select="inscription/graphics/offset/@x"/>
         </xsl:attribute>
         <xsl:attribute name="inscriptionOffsetY">
            <xsl:value-of select="inscription/graphics/offset/@y"/>
         </xsl:attribute>
         <xsl:attribute name="tagged">
            <xsl:value-of select="tagged/value"/>
         </xsl:attribute>
         <xsl:call-template name="place-transition-arc"/>
         <xsl:apply-templates select="arcpath"/>		
         <xsl:apply-templates select="type"/>		
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="stategroup">
      <xsl:element name="stategroup">
         <!-- BarryK Aug 2007 for state group storage -->
         <xsl:attribute name="id">
            <xsl:value-of select="@id"/>
         </xsl:attribute>
         <xsl:attribute name="name">
            <xsl:value-of select="name/value"/>
         </xsl:attribute>
         <xsl:apply-templates select="statecondition"/>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="statecondition">
      <xsl:element name="statecondition">
         <xsl:attribute name="value">
            <xsl:value-of select="value"/>
         </xsl:attribute>
      </xsl:element>
   </xsl:template>
        
   <xsl:template name="place-transition">
      <xsl:attribute name="name">
         <xsl:value-of select="name/value"/>
      </xsl:attribute>
      <xsl:attribute name="nameOffsetX">
         <xsl:value-of select="name/graphics/offset/@x"/>		
      </xsl:attribute>
      <xsl:attribute name="nameOffsetY">
         <xsl:value-of select="name/graphics/offset/@y"/>		
      </xsl:attribute>			
      <xsl:call-template name="place-transition-arc"/>	
   </xsl:template>	
   
   <xsl:template name="place-transition-arc">
      <xsl:attribute name="id">
         <xsl:value-of select="@id"/>
      </xsl:attribute>
      <xsl:attribute name="positionX">
         <xsl:value-of select="graphics/position/@x"/>
      </xsl:attribute>
      <xsl:attribute name="positionY">
         <xsl:value-of select="graphics/position/@y"/>
      </xsl:attribute>
    </xsl:template>
    
   <xsl:template match="arcpath">
      <xsl:element name="arcpath">
         <xsl:attribute name="x">
            <xsl:value-of select="@x"/>
         </xsl:attribute>
         <xsl:attribute name="y">
            <xsl:value-of select="@y"/>
         </xsl:attribute>
         <xsl:attribute name="arcPointType">
            <xsl:value-of select="@curvePoint"/>
         </xsl:attribute>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="type">
      <xsl:element name="type">        		
         <xsl:attribute name="type">				
            <xsl:value-of select="@value"/>
         </xsl:attribute>
      </xsl:element>
   </xsl:template>      
    
   <xsl:template match="interface">
       <xsl:element name="arc">
          <xsl:attribute name="id">                              
             InterfaceArc                        
          </xsl:attribute>
          <xsl:attribute name="positionX"/>
          <xsl:attribute name="positionY"/>
          <xsl:attribute name="source">
             <xsl:value-of select="../referencePlace/@id"/>
          </xsl:attribute>
          <xsl:attribute name="target">				
             <xsl:value-of select="importPlace/@target"/>
          </xsl:attribute>
          <xsl:attribute name="inscription"/>
          <xsl:attribute name="tagged"/>
          <xsl:attribute name="inscriptionOffsetX"/>
          <xsl:attribute name="inscriptionOffsetY"/>
       </xsl:element>
       <xsl:element name="arc">
          <xsl:attribute name="id">
             InterfaceArc
          </xsl:attribute>
          <xsl:attribute name="positionX"/>
          <xsl:attribute name="positionY"/>
          <xsl:attribute name="source">
             <xsl:value-of select="exportPlace/@id"/>
          </xsl:attribute>
          <xsl:attribute name="target">
             <xsl:value-of select="exportPlace/@ref"/>
          </xsl:attribute>
          <xsl:attribute name="inscription"/>
          <xsl:attribute name="inscriptionOffsetX"/>
          <xsl:attribute name="inscriptionOffsetY"/>
       </xsl:element>
    </xsl:template>
</xsl:stylesheet>
