package com.shorigo.db;

import java.util.List;

import android.content.Context;
import android.util.Log;
import bean.ContactArrBean;
import bean.MsgBean;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;

/**
 * 描述：数据库的操作
 */
public class DbHelper implements DbUtils.DbUpgradeListener {
	private static DbHelper dbHelper;
	private final String dbname = "shorigo";// 数据库名称
	private int version;// 数据库版本
	private DbUtils mDBClient;
	private Context context;

	private DbHelper(Context context) {
		this.context = context;
		version = Utils.getVersionCode(context);
		mDBClient = DbUtils.create(context, dbname, version, this);
		// mDBClient = DbUtils.create(context, Constants.path + Constants._path,
		// dbname, version, this);
		mDBClient.configAllowTransaction(true);
		mDBClient.configDebug(false);
	}

	public static DbHelper getInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new DbHelper(context);
		}
		return dbHelper;
	}

	@Override
	public void onUpgrade(DbUtils dbUtils, int i, int i1) {
		try {
			// 数据库的更新
			if (i < i1) {
				// 旧的版本小于新的版本
				// TODO
			}
		} catch (Exception e) {
			Log.d("DbHelper", "失败了");
		}
	}

	/******************* 增、删、改、查 ******************************/

	/**
	 * 更新用户信息
	 * 
	 * @param entity
	 *            实体类的对象
	 * @return true:插入成功 false:插入失败
	 */
	public synchronized void saveUserInfo(ContactArrBean contactArrBean, String column) {
		if (contactArrBean == null)
			return;
		ContactArrBean tempBean;
		tempBean = (ContactArrBean) searchOne(ContactArrBean.class, contactArrBean.getUser_id());
		if (tempBean == null) {
			save(contactArrBean);
		} else {
			contactArrBean.set_id(tempBean.get_id());
			update(contactArrBean, column);
		}
	}

	/**
	 * 保存联系人数据
	 * 
	 * @param entity
	 *            实体类的对象
	 * @return true:插入成功 false:插入失败
	 */
	public synchronized void saveListContactArr(List<ContactArrBean> listEntity, String column) {
		if (listEntity == null)
			return;
		ContactArrBean contactArrBean;
		ContactArrBean tempBean;
		for (int i = 0; i < listEntity.size(); i++) {
			contactArrBean = listEntity.get(i);
			tempBean = (ContactArrBean) searchOne(ContactArrBean.class, contactArrBean.getUser_id());
			if (tempBean == null) {
				save(contactArrBean);
			} else {
				contactArrBean.set_id(tempBean.get_id());
				update(contactArrBean, column);
			}
		}
	}

	/**
	 * 查询最新消息
	 * 
	 * @return 条数
	 */
	public synchronized MsgBean searchMsgBean(Class<?> entity, String value) {
		String user_id = MyConfig.getUserInfo(context).get("user_id");
		MsgBean msgBean;
		try {
			if ("10".equals(value)) {
				msgBean = mDBClient.findFirst(Selector.from(entity).where(WhereBuilder.b("type", "=", value)).orderBy("_id", true));
			} else {
				msgBean = mDBClient.findFirst(Selector.from(entity).where(WhereBuilder.b("type", "=", value).and("user_id", "=", user_id)).orderBy("_id", true));
			}
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return null;
		}
		return msgBean;
	}

	/**
	 * 查询最新消息
	 * 
	 * @return 条数
	 */
	public synchronized MsgBean searchMsgBean(Class<?> entity, String[] arrValue) {
		String user_id = MyConfig.getUserInfo(context).get("user_id");
		MsgBean msgBean;
		try {
			msgBean = mDBClient.findFirst(Selector.from(entity).where(WhereBuilder.b("type", "in", arrValue).and("user_id", "=", user_id)).orderBy("_id", true));
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return null;
		}
		return msgBean;
	}

	/**
	 * 查询最新消息
	 * 
	 * @return 条数
	 */
	public synchronized List<MsgBean> searchMsgBeanAll(Class<?> entity, String value) {
		String user_id = MyConfig.getUserInfo(context).get("user_id");
		List<MsgBean> listMsgBean;
		try {
			listMsgBean = mDBClient.findAll(Selector.from(entity).where(WhereBuilder.b("type", "=", value).and("user_id", "=", user_id)).orderBy("_id", true));
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return null;
		}
		return listMsgBean;
	}

	/**
	 * 查询最新消息
	 * 
	 * @return 条数
	 */
	public synchronized List<MsgBean> searchMsgBeanAll(Class<?> entity, String[] value) {
		String user_id = MyConfig.getUserInfo(context).get("user_id");
		List<MsgBean> listMsgBean;
		try {
			listMsgBean = mDBClient.findAll(Selector.from(entity).where(WhereBuilder.b("type", "in", value).and("user_id", "=", user_id)).orderBy("_id", true));
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return null;
		}
		return listMsgBean;
	}

	/**
	 * 查询个数
	 * 
	 * @return 条数
	 */
	public synchronized int count(Class<?> entity, String value) {
		int count = 0;
		try {
			if ("10".equals(value)) {
				count = (int) mDBClient.count(Selector.from(entity).where(WhereBuilder.b("type", "=", value).and("state", "=", "0")));
			} else {
				String user_id = MyConfig.getUserInfo(context).get("user_id");
				count = (int) mDBClient.count(Selector.from(entity).where(WhereBuilder.b("type", "=", value).and("user_id", "=", user_id).and("state", "=", "0")));
			}
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return count;
		}
		return count;
	}

	/**
	 * 查询个数
	 * 
	 * @return 条数
	 */
	public synchronized int count(Class<?> entity, String[] arrValue) {
		int count = 0;
		try {
			String user_id = MyConfig.getUserInfo(context).get("user_id");
			count = (int) mDBClient.count(Selector.from(entity).where(WhereBuilder.b("type", "in", arrValue).and("user_id", "=", user_id).and("state", "=", "0")));
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return count;
		}
		return count;
	}

	/**
	 * 清除个数
	 * 
	 * @return 条数
	 */
	public synchronized boolean clearCount(String value) {
		try {
			List<MsgBean> listMsgBean;
			if ("10".equals(value)) {
				listMsgBean = mDBClient.findAll(Selector.from(MsgBean.class).where(WhereBuilder.b("type", "=", value)));
			} else {
				String user_id = MyConfig.getUserInfo(context).get("user_id");
				listMsgBean = mDBClient.findAll(Selector.from(MsgBean.class).where(WhereBuilder.b("type", "=", value).and("user_id", "=", user_id)));
			}
			if (listMsgBean != null && listMsgBean.size() > 0) {
				MsgBean msgBean;
				for (int i = 0; i < listMsgBean.size(); i++) {
					msgBean = listMsgBean.get(i);
					msgBean.setState("1");
					mDBClient.update(msgBean, "state");
				}
			}
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 清除个数
	 * 
	 * @return 条数
	 */
	public synchronized boolean clearCount(String[] arrValue) {
		try {
			String user_id = MyConfig.getUserInfo(context).get("user_id");
			List<MsgBean> listMsgBean = mDBClient.findAll(Selector.from(MsgBean.class).where(WhereBuilder.b("type", "in", arrValue).and("user_id", "=", user_id)));
			if (listMsgBean != null && listMsgBean.size() > 0) {
				MsgBean msgBean;
				for (int i = 0; i < listMsgBean.size(); i++) {
					msgBean = listMsgBean.get(i);
					msgBean.setState("1");
					mDBClient.update(msgBean, "state");
				}
			}
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 插入单个对象
	 * 
	 * @param entity
	 *            实体类的对象
	 * @return true:插入成功 false:插入失败
	 */
	public synchronized boolean save(Object entity) {
		try {
			mDBClient.save(entity);
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 插入全部对象
	 * 
	 * @param entity
	 *            实体类的对象
	 * @return true:插入成功 false:插入失败
	 */
	public synchronized boolean saveAll(List<?> entity) {
		try {
			mDBClient.saveAll(entity);
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 保存、更新全部对象
	 * 
	 * @param entity
	 *            实体类的对象
	 * @return true:插入成功 false:插入失败
	 */
	public synchronized boolean saveOrUpdateAll(List<?> entity) {
		try {
			mDBClient.saveOrUpdateAll(entity);
		} catch (DbException e) {
			if (e != null)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除这个表中的所有数据
	 * 
	 * @param entity
	 *            实体类的对象
	 * @return true:成功 false:失败
	 */
	public synchronized boolean delete(Object entity) {
		try {
			mDBClient.delete(entity);
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 根据条件删除表
	 * 
	 * @param entity
	 *            表名称
	 * @param colun
	 *            列名
	 * @param value
	 *            值
	 * @return true:成功 false:失败
	 */
	public synchronized boolean deleteCriteria(Class<?> entity, String colun, String value) {
		try {
			mDBClient.delete(entity, WhereBuilder.b(colun, "=", value));
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 更新这张表中的所有数据
	 * 
	 * @param entity
	 *            实体类的对象
	 * @return true:更新成功 false:更新失败
	 */
	public synchronized boolean update(Object entity) {
		try {
			mDBClient.saveOrUpdate(entity);// 先去查这个条数据 根据id来判断是存储还是更新 如果存在更新
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 根据参数更新表中的数据
	 * 
	 * @param entity
	 *            实体类的对象
	 * @param value
	 *            要更新的字段
	 * @return true:更新成功 false:更新失败
	 */
	public synchronized boolean update(Object entity, String... value) {
		try {
			mDBClient.update(entity, value);
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 查找 根据id
	 * 
	 * @param cla
	 *            要查询的类
	 * @param id
	 *            要查询的id
	 * @return 查询到的数据
	 */
	public synchronized <T> Object searchOne(Class<T> cla, String user_id) {
		try {
			return mDBClient.findFirst(Selector.from(cla).where(WhereBuilder.b("user_id", "=", user_id)));
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查找 根据id
	 * 
	 * @param cla
	 *            要查询的类
	 * @param id
	 *            要查询的id
	 * @return 查询到的数据
	 */
	public synchronized <T> Object searchOne(Class<T> cla, String column, String value) {
		try {
			return mDBClient.findFirst(Selector.from(cla).where(WhereBuilder.b(column, "=", value)));
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
		}
		return null;
	}

	/**
	 * 正叙查找 没有条件的
	 * 
	 * @param entity
	 *            要查询的类
	 * @param <T>
	 *            要查询的类
	 * @return 查询到的数据
	 */
	public synchronized <T> List<T> search(Class<T> entity) {
		try {
			return mDBClient.findAll(Selector.from(entity));
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
		}
		return null;
	}

	/**
	 * 倒叙查找所有数据 没有条件的
	 * 
	 * @param entityClass
	 * @return 返回数据库中所有的表数据
	 */
	public synchronized <T> List<T> searchDesc(Class<T> entityClass) {
		try {
			return mDBClient.findAll(Selector.from(entityClass).orderBy("_id", true));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 倒叙查找所有数据 没有条件的
	 * 
	 * @param entityClass
	 *            实体类
	 * @param column
	 *            定义的查询条件
	 * @param value
	 *            定义的查询值
	 * @return 返回数据库中所有的表数据
	 */
	public synchronized <T> List<T> searchCriteria(Class<T> entityClass, String column, String value) {
		try {
			return mDBClient.findAll(Selector.from(entityClass).where(WhereBuilder.b(column, "=", value)));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 删除表格
	 * 
	 * @param entityClass
	 *            实体类
	 * @return 返回数据库中所有的表数据
	 */
	public synchronized <T> boolean drop(Class<T> entityClass) {
		try {
			mDBClient.dropTable(entityClass);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
