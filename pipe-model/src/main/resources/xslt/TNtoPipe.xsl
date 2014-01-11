<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : TNtoPipe.xsl
    Created on : 18 / juliol / 2007, 10:22
    Author     : marc
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" encoding="iso-8859-1" omit-xml-declaration="no" indent="yes"/>

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="/">
        <xsl:element name="pntools">
            <xsl:attribute name="id">Net-One</xsl:attribute>
            <xsl:attribute name="type">P/T net</xsl:attribute>
            <xsl:apply-templates select="net"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="net">
	    <xsl:apply-templates select="token"/>
        <xsl:apply-templates select="place"/>
        <xsl:apply-templates select="immediateTransition"/>
        <xsl:apply-templates select="exponentialTransition"/>
        <xsl:apply-templates select="inhibit"/>
        <xsl:apply-templates select="arc"/>
    </xsl:template>

	<xsl:template match="token">
        <xsl:element name="token">
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
			<xsl:attribute name="enabled">
                <xsl:value-of select="@enabled"/>
            </xsl:attribute>	
            <xsl:attribute name="red">
                <xsl:value-of select="@red"/>
            </xsl:attribute>
            <xsl:attribute name="green">
                <xsl:value-of select="@green"/>
            </xsl:attribute>
            <xsl:attribute name="blue">
                <xsl:value-of select="@blue"/>
            </xsl:attribute>			
        </xsl:element>	
    </xsl:template>
	
    <xsl:template match="place">
        <xsl:element name="place">
            <xsl:attribute name="initialMarking">
                <xsl:value-of select="@initialMarking"/>
            </xsl:attribute>
            <xsl:if test="@capacity">
                <xsl:attribute name="capacity">
                    <xsl:value-of select="@capacity"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="markingOffsetX">
                <xsl:value-of select="0"/>
            </xsl:attribute>
            <xsl:attribute name="markingOffsetY">
                <xsl:value-of select="0"/>
            </xsl:attribute>
            <xsl:call-template name="place-transition"/>
        </xsl:element>	
    </xsl:template>

    <xsl:template match="immediateTransition">
        <xsl:element name="transition">
            <xsl:attribute name="rate"><xsl:value-of select="@weight"/></xsl:attribute>
            <xsl:attribute name="priority"><xsl:value-of select="floor(@priority)"/></xsl:attribute>
            <xsl:attribute name="timed"><xsl:value-of select="false()"/></xsl:attribute>
            <xsl:attribute name="angle"><xsl:value-of select="graphics/@orientation"/></xsl:attribute>
            <xsl:call-template name="place-transition"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="exponentialTransition">
        <xsl:element name="transition">
            <xsl:attribute name="rate"><xsl:value-of select="1.0 div @delay"/></xsl:attribute>
            <xsl:attribute name="timed"><xsl:value-of select="true()"/></xsl:attribute>
            <xsl:if test="@serverType='InfiniteServer'">
                <xsl:attribute name="infiniteServer">true</xsl:attribute>
            </xsl:if>
            <xsl:if test="@serverType='ExclusiveServer'">
                <xsl:attribute name="infiniteServer">false</xsl:attribute>
            </xsl:if>
            <xsl:attribute name="angle"><xsl:value-of select="graphics/@orientation"/></xsl:attribute>
            <xsl:call-template name="place-transition"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="arc">
        <xsl:element name="arc">
            <xsl:attribute name="source">
                <xsl:value-of select="@fromNode"/>
            </xsl:attribute>
            <xsl:attribute name="target">
                <xsl:value-of select="@toNode"/>
            </xsl:attribute>
            <xsl:attribute name="inscription">
                <xsl:value-of select="@weight"/>
            </xsl:attribute>
            <xsl:attribute name="inscriptionOffsetX">
                <xsl:value-of select="inscription/graphics/@x"/>
            </xsl:attribute>
            <xsl:attribute name="inscriptionOffsetY">
                <xsl:value-of select="inscription/graphics/@y"/>
            </xsl:attribute>
            <xsl:call-template name="place-transition-arc"/>
            <xsl:element name="type">
                <xsl:attribute name="type">normal</xsl:attribute>
            </xsl:element>
            <!--<xsl:apply-templates select="arcpath"/>-->
        </xsl:element>
    </xsl:template>
    <xsl:template match="inhibit">
        <xsl:element name="arc">
            <xsl:attribute name="source">
                <xsl:value-of select="@fromNode"/>
            </xsl:attribute>
            <xsl:attribute name="target">
                <xsl:value-of select="@toNode"/>
            </xsl:attribute>
            <xsl:attribute name="inscription">
                <xsl:value-of select="inscription/@text"/>
            </xsl:attribute>
            <xsl:attribute name="inscriptionOffsetX">
                <xsl:value-of select="inscription/graphics/@x"/>
            </xsl:attribute>
            <xsl:attribute name="inscriptionOffsetY">
                <xsl:value-of select="inscription/graphics/@y"/>
            </xsl:attribute>
            <xsl:call-template name="place-transition-arc"/>
            <xsl:element name="type">
                <xsl:attribute name="type">inhibitor</xsl:attribute>
            </xsl:element>
            <!--<xsl:apply-templates select="arcpath"/>-->
        </xsl:element>
    </xsl:template>
    <xsl:template name="place-transition">
        <xsl:attribute name="name">
            <xsl:value-of select="label/@text"/>
        </xsl:attribute>
        <xsl:attribute name="nameOffsetX">
            -20.0
            <!--<xsl:value-of select="label/graphics/@x"/>-->
        </xsl:attribute>
        <xsl:attribute name="nameOffsetY">
            10.0
            <!--<xsl:value-of select="label/graphics/@y"/>-->
        </xsl:attribute>				
        <xsl:call-template name="place-transition-arc"/>
    </xsl:template>	

    <xsl:template name="place-transition-arc">
        <xsl:attribute name="id">
                    <xsl:value-of select="@id"/>
            </xsl:attribute>	
            <xsl:attribute name="positionX">
                    <xsl:value-of select="graphics/@x"/>
            </xsl:attribute>
            <xsl:attribute name="positionY">
                    <xsl:value-of select="graphics/@y"/>
        </xsl:attribute>
    </xsl:template>

</xsl:stylesheet>
