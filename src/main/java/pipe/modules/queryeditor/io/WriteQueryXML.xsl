<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="iso-8859-1" omit-xml-declaration="no" indent="yes" version="1.0"/>

	<xsl:template match="/">		
		<xsl:element name="ptml">	
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="tree">		
		<xsl:element name="tree">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="node">	
		<xsl:element name="node">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:value-of select="@type"/>
			</xsl:attribute>
			<xsl:attribute name="operation">
				<xsl:value-of select="@operation"/>
			</xsl:attribute>
			<xsl:attribute name="label">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
			<xsl:attribute name="x">
				<xsl:value-of select="@x"/>
			</xsl:attribute>
			<xsl:attribute name="y">
				<xsl:value-of select="@y"/>
			</xsl:attribute>
			<xsl:element name="incomingArc">
				<xsl:value-of select="@incomingArc"/>
			</xsl:element>
			<xsl:apply-templates/>		
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="outgoingArcs">	
		<xsl:element name="outgoingArcs">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="childNodes">	
		<xsl:element name="childNodes">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="arc">
		<xsl:element name="arc">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="label">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
			<xsl:attribute name="source">
				<xsl:value-of select="@source"/>
			</xsl:attribute>
			<xsl:attribute name="target">
				<xsl:value-of select="@target"/>
			</xsl:attribute>
			<xsl:attribute name="required">
				<xsl:value-of select="@required"/>
			</xsl:attribute>	
			<xsl:attribute name="startX">
				<xsl:value-of select="@startX"/>
			</xsl:attribute>
			<xsl:attribute name="startY">
				<xsl:value-of select="@startY"/>
			</xsl:attribute>
			<xsl:attribute name="endX">
				<xsl:value-of select="@endX"/>
			</xsl:attribute>
			<xsl:attribute name="endY">
				<xsl:value-of select="@endY"/>
			</xsl:attribute>	
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
		
	<xsl:template match="arcpathpoint">
		<xsl:element name = "arcpathpoint">
			<xsl:attribute name = "id">
				<xsl:value-of select ="@id"/>
			</xsl:attribute>
			<xsl:attribute name = "type">
				<xsl:value-of select="@type"/>
			</xsl:attribute>
			<xsl:attribute name = "x">
				<xsl:value-of select ="@x"/>
			</xsl:attribute>
			<xsl:attribute name = "y">
				<xsl:value-of select ="@y"/>
			</xsl:attribute>	
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="stateLabels">	
		<xsl:element name="stateLabels">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="statelabel">	
		<xsl:element name="statelabel">
			<xsl:attribute name="name">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="state">	
		<xsl:element name="state">
			<xsl:attribute name="name">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="actionLabels">	
		<xsl:element name="actionLabels">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="actionlabel">	
		<xsl:element name="actionlabel">
			<xsl:attribute name="label">
				<xsl:value-of select="@label"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="macro">		
		<xsl:element name="macro">
			<xsl:attribute name="name">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<xsl:attribute name="description">
				<xsl:value-of select="@description"/>
			</xsl:attribute>
			<xsl:attribute name="returntype">
				<xsl:value-of select="@returntype"/>
			</xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>