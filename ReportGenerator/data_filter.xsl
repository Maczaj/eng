<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <DOCUMENT>
            <xsl:apply-templates></xsl:apply-templates>
        </DOCUMENT>
    </xsl:template>

    <xsl:template match="ROW[position()=1]" >
        <ROWSET>
                  <xsl:copy-of select="." />
            </ROWSET>
    </xsl:template>

    <xsl:template match="ROW[position()>1]" />

    <xsl:template match="/DOCUMENT/*[count(child::*)=0 ]" >
        <xsl:copy-of select="." />
    </xsl:template>
</xsl:stylesheet>