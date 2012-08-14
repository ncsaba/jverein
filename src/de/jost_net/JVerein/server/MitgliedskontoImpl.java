/**********************************************************************
 * $Source$
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (c) by Heiner Jostkleigrewe
 * This program is free software: you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,  but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
 *  the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, 
 * see <http://www.gnu.org/licenses/>.
 * 
 * heiner@jverein.de
 * www.jverein.de
 **********************************************************************/
package de.jost_net.JVerein.server;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import de.jost_net.JVerein.Einstellungen;
import de.jost_net.JVerein.JVereinPlugin;
import de.jost_net.JVerein.rmi.Abrechnungslauf;
import de.jost_net.JVerein.rmi.Mitglied;
import de.jost_net.JVerein.rmi.Mitgliedskonto;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ResultSetExtractor;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class MitgliedskontoImpl extends AbstractDBObject implements
    Mitgliedskonto
{

  private static final long serialVersionUID = -1234L;

  private Double ist = null;

  public MitgliedskontoImpl() throws RemoteException
  {
    super();
  }

  @Override
  protected String getTableName()
  {
    return "mitgliedskonto";
  }

  @Override
  public String getPrimaryAttribute()
  {
    return "mitglied";
  }

  @Override
  protected void deleteCheck()
  {
    //
  }

  @Override
  protected void insertCheck() throws ApplicationException
  {
    try
    {
      if (getZweck1().length() == 0)
      {
        throw new ApplicationException(JVereinPlugin.getI18n().tr(
            "Verwendungszweck fehlt"));
      }
      if (getBetrag() == null)
      {
        String fehler = JVereinPlugin.getI18n().tr("Betrag fehlt");
        Logger.error(fehler);
        throw new ApplicationException(fehler);
      }

    }
    catch (RemoteException e)
    {
      String fehler = JVereinPlugin.getI18n().tr(
          "Mitgliedskonto kann nicht gespeichert werden. Siehe system log");
      Logger.error(fehler, e);
      throw new ApplicationException(fehler);
    }
  }

  @Override
  protected void updateCheck() throws ApplicationException
  {
    insertCheck();
  }

  @Override
  protected Class<?> getForeignObject(String arg0)
  {
    if ("mitglied".equals(arg0))
    {
      return Mitglied.class;
    }
    if ("abrechnungslauf".equals(arg0))
    {
      return Abrechnungslauf.class;
    }
    return null;
  }

  public Abrechnungslauf getAbrechnungslauf() throws RemoteException
  {
    return (Abrechnungslauf) getAttribute("abrechnungslauf");
  }

  public void setAbrechnungslauf(Abrechnungslauf abrechnungslauf)
      throws RemoteException
  {
    setAttribute("abrechnungslauf", new Integer(abrechnungslauf.getID()));
  }

  public Mitglied getMitglied() throws RemoteException
  {
    return (Mitglied) getAttribute("mitglied");
  }

  public void setMitglied(Mitglied mitglied) throws RemoteException
  {
    setAttribute("mitglied", new Integer(mitglied.getID()));
  }

  public Date getDatum() throws RemoteException
  {
    return (Date) getAttribute("datum");
  }

  public void setDatum(Date datum) throws RemoteException
  {
    setAttribute("datum", datum);
  }

  public String getZweck1() throws RemoteException
  {
    return (String) getAttribute("zweck1");
  }

  public void setZweck1(String zweck1) throws RemoteException
  {
    setAttribute("zweck1", zweck1);
  }

  public Integer getZahlungsweg() throws RemoteException
  {
    return (Integer) getAttribute("zahlungsweg");
  }

  public void setZahlungsweg(Integer zahlungsweg) throws RemoteException
  {
    setAttribute("zahlungsweg", zahlungsweg);
  }

  public Double getBetrag() throws RemoteException
  {
    return (Double) getAttribute("betrag");
  }

  public void setBetrag(Double d) throws RemoteException
  {
    setAttribute("betrag", d);
  }

  public Double getIstSumme() throws RemoteException
  {
    if (ist != null)
    {
      return ist;
    }

    DBService service = Einstellungen.getDBService();
    String sql = "select sum(betrag) from buchung where mitgliedskonto = "
        + this.getID();

    ResultSetExtractor rs = new ResultSetExtractor()
    {
      public Object extract(ResultSet rs) throws SQLException
      {
        if (!rs.next())
        {
          return new Double(0.0d);
        }
        return Double.valueOf(rs.getDouble(1));
      }
    };
    ist = new Double((Double) service.execute(sql, new Object[] {}, rs));
    return ist;
  }

  @Override
  public Object getAttribute(String fieldName) throws RemoteException
  {
    if (fieldName.equals("istsumme"))
    {
      return getIstSumme();
    }
    return super.getAttribute(fieldName);
  }
}
