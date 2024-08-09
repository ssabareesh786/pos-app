<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <xsl:template match="invoice">
    <fo:root>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21.0cm" margin-top="1cm" margin-left="2cm" margin-right="2cm" margin-bottom="1cm">
          <!-- Page template goes here -->
          <fo:region-body region-name="xsl-region-body" />
          <fo:region-before region-name="xsl-region-before" extent="3cm"/>
          <fo:region-after region-name="xsl-region-after" extent="4cm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="A4">
        <!-- Page content goes here -->
        <fo:static-content flow-name="xsl-region-before">
          <fo:block>
            <fo:table>
              <fo:table-column column-width="8.5cm"/>
              <fo:table-column column-width="8.5cm"/>
              <fo:table-body>
                <fo:table-row font-size="18pt" line-height="30px" background-color="#3e73b9" color="white">
                  <fo:table-cell padding-left="5pt">
                    <fo:block>
                      INCREFF
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block text-align="right">
                      INVOICE
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block>
          <fo:block space-before="20pt"></fo:block>
        </fo:static-content>

        <fo:static-content flow-name="xsl-region-after">
          <fo:block line-height="20pt">
            <fo:block font-weight="bold">
              Copyright Increff 2019
            </fo:block>
          </fo:block>
        </fo:static-content>

        <fo:flow flow-name="xsl-region-body">
          <fo:block space-before="20pt" width="17cm" >
            <fo:table>
              <fo:table-column column-width="7cm"/>
              <fo:table-column column-width="10cm"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block text-align="right">
                      INVOICE DATE
                    </fo:block>
                    <fo:block text-align="right">
                      ORDER ID
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block text-align="right">
                      <xsl:value-of select="date"/>
                      <xsl:value-of select="orderId"/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block>
          <fo:block space-before="20pt">
            <fo:table line-height="30px">
              <fo:table-column column-width="1cm"/>
              <fo:table-column column-width="5cm"/>
              <fo:table-column column-width="2cm"/>
              <fo:table-column column-width="3cm"/>
              <fo:table-column column-width="3cm"/>
              <fo:table-column column-width="3cm"/>
              <fo:table-header>
                <fo:table-row background-color="#f5f5f5" text-align="center" font-weight="bold">
                  <fo:table-cell border="1px solid #b8b6b6">
                    <fo:block>SNo</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="1px solid #b8b6b6">
                    <fo:block>PRODUCT NAME</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="1px solid #b8b6b6">
                    <fo:block>QTY</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="1px solid #b8b6b6">
                    <fo:block>MRP</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="1px solid #b8b6b6">
                    <fo:block>SELLING PRICE</fo:block>
                  </fo:table-cell>
                  <fo:table-cell border="1px solid #b8b6b6">
                    <fo:block>SUB TOTAL</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-header>
              <fo:table-body>
                <xsl:apply-templates select="item"/>
                <fo:table-row font-weight="bold">
                  <fo:table-cell number-columns-spanned="5" text-align="right" padding-right="3pt">
                    <fo:block>Total</fo:block>
                  </fo:table-cell>
                  <fo:table-cell  text-align="right" padding-right="3pt" background-color="#f5f5f5" border="1px solid #b8b6b6" >
                    <fo:block>
                      <xsl:value-of select="total" />
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
          </fo:block>
        </fo:flow>

      </fo:page-sequence>
    </fo:root>
  </xsl:template>

  <xsl:template match="item">
    <fo:table-row>
      <fo:table-cell border="1px solid #b8b6b6" text-align="center">
        <fo:block>
          <xsl:value-of select="sno"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell border="1px solid #b8b6b6" padding-left="3pt">
        <fo:block>
          <xsl:value-of select="productName"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell border="1px solid #b8b6b6" text-align="right" padding-right="3pt">
        <fo:block>
          <xsl:value-of select="quantity"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell border="1px solid #b8b6b6" text-align="right" padding-right="3pt">
        <fo:block>
          <xsl:value-of select="mrp"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell border="1px solid #b8b6b6" text-align="right" padding-right="3pt">
        <fo:block>
          <xsl:value-of select="sellingPrice"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell border="1px solid #b8b6b6" text-align="right" padding-right="3pt">
        <fo:block>
          <xsl:value-of select="subTotal"/>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>

  </xsl:template>
</xsl:stylesheet>