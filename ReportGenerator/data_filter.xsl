<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="ROW[position()=1]" >
        <DOCUMENT>\n
            <ROWSET>\n
                  <xsl:copy-of select="." />
            </ROWSET>\n
        </DOCUMENT>\n
    </xsl:template>

    <xsl:template match="ROW[position()>1]" />

    <xsl:template match="/DOCUMENT/*[count(child::*)=0 ]" >
        <xsl:copy-of select="." />
    </xsl:template>
</xsl:stylesheet>