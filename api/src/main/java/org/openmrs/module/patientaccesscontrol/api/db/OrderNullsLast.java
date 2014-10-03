package org.openmrs.module.patientaccesscontrol.api.db;

import java.sql.Types;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;
import org.openmrs.api.db.hibernate.HibernateUtil;

public class OrderNullsLast extends Order {
	
	private static final long serialVersionUID = -5905007878883095416L;
	
	private boolean ascending;
	
	private boolean ignoreCase;
	
	private String propertyName;
	
	@Override
	public String toString() {
		return propertyName + ' ' + (ascending ? "asc" : "desc") + " NULLS LAST";
	}
	
	/**
	 * Constructor for OrderNullsLast.
	 */
	protected OrderNullsLast(String propertyName, boolean ascending) {
		super(propertyName, ascending);
		this.propertyName = propertyName;
		this.ascending = ascending;
	}
	
	/**
	 * Render the SQL fragment
	 */
	@Override
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		String[] columns = criteriaQuery.getColumnsUsingProjection(criteria, propertyName);
		Type type = criteriaQuery.getTypeUsingProjection(criteria, propertyName);
		SessionFactoryImplementor factory = criteriaQuery.getFactory();
		boolean isMySQLDialect = isMySQLDialect(factory);
		StringBuffer fragment = new StringBuffer();
		for (int i = 0; i < columns.length; i++) {
			if (isMySQLDialect) {
				fragment.append("ISNULL(" + columns[i] + "), ");
			}
			boolean lower = ignoreCase && type.sqlTypes(factory)[i] == Types.VARCHAR;
			if (lower) {
				fragment.append(factory.getDialect().getLowercaseFunction()).append('(');
			}
			fragment.append(columns[i]);
			if (lower) {
				fragment.append(')');
			}
			fragment.append(ascending ? " asc" : " desc");
			if (!isMySQLDialect) {
				fragment.append(" NULLS LAST");
			}
			
			if (i < columns.length - 1) {
				fragment.append(", ");
			}
		}
		return fragment.toString();
	}
	
	private boolean isMySQLDialect(SessionFactory sessionFactory) {
		return MySQLDialect.class.getName().equals(HibernateUtil.getDialect(sessionFactory).getClass().getName())
		        || MySQL5Dialect.class.getName().equals(HibernateUtil.getDialect(sessionFactory).getClass().getName());
	}
	
	/**
	 * Ascending order
	 * 
	 * @param propertyName
	 * @return OrderNullsLast
	 */
	public static OrderNullsLast asc(String propertyName) {
		return new OrderNullsLast(propertyName, true);
	}
	
	/**
	 * Descending order
	 * 
	 * @param propertyName
	 * @return OrderNullsLast
	 */
	public static OrderNullsLast desc(String propertyName) {
		return new OrderNullsLast(propertyName, false);
	}
}
