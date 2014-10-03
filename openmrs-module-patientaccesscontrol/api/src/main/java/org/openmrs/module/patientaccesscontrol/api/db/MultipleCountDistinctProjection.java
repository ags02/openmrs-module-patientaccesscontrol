package org.openmrs.module.patientaccesscontrol.api.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.AggregateProjection;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.openmrs.api.db.hibernate.HibernateUtil;

public class MultipleCountDistinctProjection extends AggregateProjection {
	
	private static final long serialVersionUID = -6573601486964358989L;
	
	private List<String> properties = new ArrayList<String>();
	
	private List<Boolean> includeNulls = new ArrayList<Boolean>();
	
	protected MultipleCountDistinctProjection(String prop) {
		this(prop, false);
	}
	
	protected MultipleCountDistinctProjection(String prop, boolean includeNull) {
		super("count", prop);
		properties.add(prop);
		includeNulls.add(includeNull);
	}
	
	@Override
	public String toString() {
		return "distinct " + properties.toString();
	}
	
	@Override
	public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		return new Type[] { LongType.INSTANCE };
	}
	
	@Override
	public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
		boolean isMySQLDialect = isMySQLDialect(criteriaQuery.getFactory());
		StringBuffer buf = new StringBuffer();
		buf.append("count(");
		buf.append("distinct ");
		SQLFunction coalesceFunction = criteriaQuery.getFactory().getDialect().getFunctions().get("coalesce");
		SQLFunction concatFunction = criteriaQuery.getFactory().getDialect().getFunctions().get("concat");
		List<String> params = new ArrayList<String>();
		for (int i = 0; i < properties.size(); i++) {
			String column = criteriaQuery.getColumn(criteria, properties.get(i));
			boolean includeNull = includeNulls.get(i);
			if (includeNull) {
				column = coalesceFunction
				        .render(StringType.INSTANCE, Arrays.asList(column, "0"), criteriaQuery.getFactory());
			}
			if (isMySQLDialect) {
				buf.append(column);
				if (i != properties.size() - 1) {
					buf.append(", ");
				}
			} else {
				params.add(column);
				if (i != properties.size() - 1) {
					params.add("'-'");
				}
			}
		}
		if (!isMySQLDialect) {
			buf.append(concatFunction.render(StringType.INSTANCE, params, criteriaQuery.getFactory()));
		}
		buf.append(") as y");
		buf.append(position);
		buf.append('_');
		return buf.toString();
	}
	
	private boolean isMySQLDialect(SessionFactory sessionFactory) {
		return MySQLDialect.class.getName().equals(HibernateUtil.getDialect(sessionFactory).getClass().getName())
		        || MySQL5Dialect.class.getName().equals(HibernateUtil.getDialect(sessionFactory).getClass().getName());
	}
	
	public MultipleCountDistinctProjection addProperty(String property) {
		return addProperty(property, false);
	}
	
	public MultipleCountDistinctProjection addProperty(String property, boolean includeNull) {
		properties.add(property);
		includeNulls.add(includeNull);
		return this;
	}
	
}
