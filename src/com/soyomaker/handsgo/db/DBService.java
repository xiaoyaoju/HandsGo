/*
 * 
 */
package com.soyomaker.handsgo.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.go.Group;

/**
 * The Class DBService.
 */
public class DBService {

	/** The s db open helper. */
	private static DBOpenHelper mDbOpenHelper;

	/** The m chess manual caches. */
	private static ArrayList<ChessManual> mFavoriteChessManualCaches = new ArrayList<ChessManual>();

	/** The m chess manual caches. */
	private static ArrayList<ChessManual> mHistoryChessManualCaches = new ArrayList<ChessManual>();

	/** The m groups caches. */
	private static ArrayList<Group> mGroupsCaches = new ArrayList<Group>();

	/** The should update chache. */
	private static boolean shouldUpdateFavoriteCache = true;

	/** The should update chache. */
	private static boolean shouldUpdateHistoryCache = true;

	/** The should update group cache. */
	private static boolean shouldUpdateGroupCache = true;

	/**
	 * Inits the.
	 * 
	 * @param context
	 *            the context
	 * @return void 返回类型
	 * @Title: openDataBase
	 * @Description: 确保数据库第一次打开是写模式，否则编辑和升级数据库会失败
	 */
	public synchronized static void init(Context context) {
		mDbOpenHelper = new DBOpenHelper(context);
		mDbOpenHelper.getWritableDatabase();
	}

	/**
	 * Gets the history chess manual caches.
	 * 
	 * @return the history chess manual caches
	 */
	public static ArrayList<ChessManual> getHistoryChessManualCaches() {
		if (shouldUpdateHistoryCache) {
			mHistoryChessManualCaches = getAllHistoryChessManual();
			shouldUpdateHistoryCache = false;
		}
		return mHistoryChessManualCaches;
	}

	/**
	 * Gets the favorite chess manual caches.
	 * 
	 * @return the favorite chess manual caches
	 */
	public static ArrayList<ChessManual> getFavoriteChessManualCaches() {
		if (shouldUpdateFavoriteCache) {
			mFavoriteChessManualCaches = getAllFavoriteChessManual();
			shouldUpdateFavoriteCache = false;
		}
		return mFavoriteChessManualCaches;
	}

	/**
	 * Gets the group caches.
	 * 
	 * @return the group caches
	 */
	public static ArrayList<Group> getGroupCaches() {
		if (shouldUpdateGroupCache) {
			mGroupsCaches = getAllGroup();
			shouldUpdateGroupCache = false;
		}
		return mGroupsCaches;
	}

	/**
	 * Checks if is history chess manual.
	 * 
	 * @param chessManual
	 *            the chess manual
	 * @return true, if is history chess manual
	 */
	public static boolean isHistoryChessManual(ChessManual chessManual) {
		if (chessManual == null || chessManual.getSgfUrl() == null) {
			return false;
		}
		ArrayList<ChessManual> chessManuals = getHistoryChessManualCaches();
		for (ChessManual cManual : chessManuals) {
			if (chessManual.equals(cManual)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is favorite chess manual.
	 * 
	 * @param chessManual
	 *            the chess manual
	 * @return true, if is favorite chess manual
	 */
	public static boolean isFavoriteChessManual(ChessManual chessManual) {
		if (chessManual == null || chessManual.getSgfUrl() == null) {
			return false;
		}
		ArrayList<ChessManual> chessManuals = getFavoriteChessManualCaches();
		for (ChessManual cManual : chessManuals) {
			if (chessManual.equals(cManual)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the all favorite chess manual.
	 * 
	 * @return the all favorite chess manual
	 */
	public synchronized static ArrayList<Group> getAllGroup() {
		ArrayList<Group> groups = new ArrayList<Group>();
		Cursor cursor = null;
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
		String sql = "select * from " + DBOpenHelper.GROUP_TABLE_NAME;
		if (db == null) {
			return groups;
		}
		try {
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					Group group = new Group();
					group.setId(cursor.getInt(0));
					group.setName(cursor.getString(1));
					group.setChessManuals(getFavoriteChessManualsByGroupId(group
							.getId()));
					groups.add(group);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return groups;
	}

	/**
	 * Gets the all favorite chess manual.
	 * 
	 * @return the all favorite chess manual
	 */
	public synchronized static ArrayList<ChessManual> getAllHistoryChessManual() {
		ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
		Cursor cursor = null;
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
		String sql = "select * from " + DBOpenHelper.HISTORY_TABLE_NAME;
		if (db == null) {
			return chessManuals;
		}
		try {
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					ChessManual chessManual = new ChessManual();
					chessManual.setId(cursor.getInt(0));
					chessManual.setSgfUrl(cursor.getString(1));
					chessManual.setBlackName(cursor.getString(2));
					chessManual.setWhiteName(cursor.getString(3));
					chessManual.setMatchName(cursor.getString(4));
					chessManual.setMatchResult(cursor.getString(5));
					chessManual.setMatchTime(cursor.getString(6));
					chessManual.setCharset(cursor.getString(7));
					chessManual.setSgfContent(cursor.getString(8));
					chessManual.setType(cursor.getInt(9));
					chessManual.setGroupId(cursor.getInt(10));
					chessManuals.add(chessManual);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return chessManuals;
	}

	/**
	 * Gets the all favorite chess manual.
	 * 
	 * @return the all favorite chess manual
	 */
	public synchronized static ArrayList<ChessManual> getAllFavoriteChessManual() {
		ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
		Cursor cursor = null;
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
		String sql = "select * from " + DBOpenHelper.FAVORITE_TABLE_NAME;
		if (db == null) {
			return chessManuals;
		}
		try {
			cursor = db.rawQuery(sql, null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					ChessManual chessManual = new ChessManual();
					chessManual.setId(cursor.getInt(0));
					chessManual.setSgfUrl(cursor.getString(1));
					chessManual.setBlackName(cursor.getString(2));
					chessManual.setWhiteName(cursor.getString(3));
					chessManual.setMatchName(cursor.getString(4));
					chessManual.setMatchResult(cursor.getString(5));
					chessManual.setMatchTime(cursor.getString(6));
					chessManual.setCharset(cursor.getString(7));
					chessManual.setSgfContent(cursor.getString(8));
					chessManual.setType(cursor.getInt(9));
					chessManual.setGroupId(cursor.getInt(10));
					chessManuals.add(chessManual);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return chessManuals;
	}

	/**
	 * Gets the chess manuals by group id.
	 * 
	 * @param groudId
	 *            the groud id
	 * @return the chess manuals by group id
	 */
	public synchronized static ArrayList<ChessManual> getFavoriteChessManualsByGroupId(
			int groudId) {
		ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
		Cursor cursor = null;
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
		String sql = "select * from " + DBOpenHelper.FAVORITE_TABLE_NAME
				+ " where groupId = ?";
		if (db == null) {
			return chessManuals;
		}
		try {
			cursor = db.rawQuery(sql, new String[] { "" + groudId });
			if (cursor != null && cursor.moveToFirst()) {
				do {
					ChessManual chessManual = new ChessManual();
					chessManual.setId(cursor.getInt(0));
					chessManual.setSgfUrl(cursor.getString(1));
					chessManual.setBlackName(cursor.getString(2));
					chessManual.setWhiteName(cursor.getString(3));
					chessManual.setMatchName(cursor.getString(4));
					chessManual.setMatchResult(cursor.getString(5));
					chessManual.setMatchTime(cursor.getString(6));
					chessManual.setCharset(cursor.getString(7));
					chessManual.setSgfContent(cursor.getString(8));
					chessManual.setType(cursor.getInt(9));
					chessManual.setGroupId(cursor.getInt(10));
					chessManuals.add(chessManual);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return chessManuals;
	}

	/**
	 * Gets the history chess manual.
	 * 
	 * @param chessManual
	 *            the chess manual
	 * @return the history chess manual
	 */
	public synchronized static ChessManual getHistoryChessManual(
			ChessManual chessManual) {
		if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
			return null;
		}
		Cursor cursor = null;
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
		String sql = "select * from " + DBOpenHelper.HISTORY_TABLE_NAME
				+ " where sgfUrl = ?";
		if (db == null) {
			return null;
		}

		ChessManual newChessManual = null;
		try {
			cursor = db.rawQuery(sql, new String[] { chessManual.getSgfUrl() });
			if (cursor != null && cursor.moveToFirst()) {
				newChessManual = new ChessManual();
				newChessManual.setId(cursor.getInt(0));
				newChessManual.setSgfUrl(cursor.getString(1));
				newChessManual.setBlackName(cursor.getString(2));
				newChessManual.setWhiteName(cursor.getString(3));
				newChessManual.setMatchName(cursor.getString(4));
				newChessManual.setMatchResult(cursor.getString(5));
				newChessManual.setMatchTime(cursor.getString(6));
				newChessManual.setCharset(cursor.getString(7));
				newChessManual.setSgfContent(cursor.getString(8));
				newChessManual.setType(cursor.getInt(9));
				newChessManual.setGroupId(cursor.getInt(10));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return newChessManual;
	}

	/**
	 * Gets the favorite chess manual.
	 * 
	 * @param chessManual
	 *            the chess manual
	 * @return the favorite chess manual
	 */
	public synchronized static ChessManual getFavoriteChessManual(
			ChessManual chessManual) {
		if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
			return null;
		}
		Cursor cursor = null;
		SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
		String sql = "select * from " + DBOpenHelper.FAVORITE_TABLE_NAME
				+ " where sgfUrl = ?";
		if (db == null) {
			return null;
		}

		ChessManual newChessManual = null;
		try {
			cursor = db.rawQuery(sql, new String[] { chessManual.getSgfUrl() });
			if (cursor != null && cursor.moveToFirst()) {
				newChessManual = new ChessManual();
				newChessManual.setId(cursor.getInt(0));
				newChessManual.setSgfUrl(cursor.getString(1));
				newChessManual.setBlackName(cursor.getString(2));
				newChessManual.setWhiteName(cursor.getString(3));
				newChessManual.setMatchName(cursor.getString(4));
				newChessManual.setMatchResult(cursor.getString(5));
				newChessManual.setMatchTime(cursor.getString(6));
				newChessManual.setCharset(cursor.getString(7));
				newChessManual.setSgfContent(cursor.getString(8));
				newChessManual.setType(cursor.getInt(9));
				newChessManual.setGroupId(cursor.getInt(10));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return newChessManual;
	}

	/**
	 * Save group.
	 * 
	 * @param group
	 *            the group
	 * @return true, if successful
	 */
	public synchronized static boolean saveGroup(Group group) {
		if (TextUtils.isEmpty(group.getName())) {
			return false;
		}
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		if (db == null) {
			return false;
		}
		try {
			if (group.getId() < 0) {
				db.execSQL("insert into " + DBOpenHelper.GROUP_TABLE_NAME
						+ " (name) values (?)",
						new Object[] { group.getName() });
				Cursor cursor = db.rawQuery("select LAST_INSERT_ROWID()", null);
				if (cursor != null && cursor.moveToFirst()) {
					group.setId(cursor.getInt(0));
				}
				if (cursor != null) {
					cursor.close();
				}
			} else {
				db.execSQL("update " + DBOpenHelper.GROUP_TABLE_NAME
						+ " set name = ? where _id = ?",
						new Object[] { group.getName(), group.getId() });
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		shouldUpdateGroupCache = true;
		return true;
	}

	/**
	 * Save chess manual.
	 * 
	 * @param chessManual
	 *            the chess manual
	 * @return true, if successful
	 */
	public synchronized static boolean saveHistoryChessManual(
			ChessManual chessManual) {
		if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
			return false;
		}
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		if (db == null) {
			return false;
		}
		try {
			String sql = "select * from " + DBOpenHelper.HISTORY_TABLE_NAME
					+ " where sgfUrl = ?";
			Cursor cursor = db.rawQuery(sql,
					new String[] { chessManual.getSgfUrl() });
			if (cursor != null && cursor.getCount() > 0) {
				db.execSQL(
						"update "
								+ DBOpenHelper.HISTORY_TABLE_NAME
								+ " set blackName = ?,whiteName = ?,matchName = ?,matchResult = ?,"
								+ "matchTime = ?,charset = ?,sgfContent = ?,type = ?,groupId = ? where sgfUrl = ?",
						new Object[] { chessManual.getBlackName(),
								chessManual.getWhiteName(),
								chessManual.getMatchName(),
								chessManual.getMatchResult(),
								chessManual.getMatchTime(),
								chessManual.getCharset(),
								chessManual.getSgfContent(),
								chessManual.getType(),
								chessManual.getGroupId(),
								chessManual.getSgfUrl() });
			} else {
				db.execSQL(
						"insert into "
								+ DBOpenHelper.HISTORY_TABLE_NAME
								+ " (sgfUrl,blackName,whiteName,matchName,matchResult,"
								+ "matchTime,charset,sgfContent,type,groupId) values (?,?,?,?,?,?,?,?,?,?)",
						new Object[] { chessManual.getSgfUrl(),
								chessManual.getBlackName(),
								chessManual.getWhiteName(),
								chessManual.getMatchName(),
								chessManual.getMatchResult(),
								chessManual.getMatchTime(),
								chessManual.getCharset(),
								chessManual.getSgfContent(),
								chessManual.getType(), chessManual.getGroupId() });
			}
			if (cursor != null) {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		shouldUpdateHistoryCache = true;
		return true;
	}

	/**
	 * Save chess manual.
	 * 
	 * @param chessManual
	 *            the chess manual
	 * @return true, if successful
	 */
	public synchronized static boolean saveFavoriteChessManual(
			ChessManual chessManual) {
		if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
			return false;
		}
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		if (db == null) {
			return false;
		}
		try {
			String sql = "select * from " + DBOpenHelper.FAVORITE_TABLE_NAME
					+ " where sgfUrl = ?";
			Cursor cursor = db.rawQuery(sql,
					new String[] { chessManual.getSgfUrl() });
			if (cursor != null && cursor.getCount() > 0) {
				db.execSQL(
						"update "
								+ DBOpenHelper.FAVORITE_TABLE_NAME
								+ " set blackName = ?,whiteName = ?,matchName = ?,matchResult = ?,"
								+ "matchTime = ?,charset = ?,sgfContent = ?,type = ?,groupId = ? where sgfUrl = ?",
						new Object[] { chessManual.getBlackName(),
								chessManual.getWhiteName(),
								chessManual.getMatchName(),
								chessManual.getMatchResult(),
								chessManual.getMatchTime(),
								chessManual.getCharset(),
								chessManual.getSgfContent(),
								chessManual.getType(),
								chessManual.getGroupId(),
								chessManual.getSgfUrl() });
			} else {
				db.execSQL(
						"insert into "
								+ DBOpenHelper.FAVORITE_TABLE_NAME
								+ " (sgfUrl,blackName,whiteName,matchName,matchResult,"
								+ "matchTime,charset,sgfContent,type,groupId) values (?,?,?,?,?,?,?,?,?,?)",
						new Object[] { chessManual.getSgfUrl(),
								chessManual.getBlackName(),
								chessManual.getWhiteName(),
								chessManual.getMatchName(),
								chessManual.getMatchResult(),
								chessManual.getMatchTime(),
								chessManual.getCharset(),
								chessManual.getSgfContent(),
								chessManual.getType(), chessManual.getGroupId() });
			}
			if (cursor != null) {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		shouldUpdateGroupCache = true;
		shouldUpdateFavoriteCache = true;
		return true;
	}

	/**
	 * Delete group.
	 * 
	 * @param group
	 *            the group
	 * @return true, if successful
	 */
	public synchronized static boolean deleteGroup(Group group) {
		if (TextUtils.isEmpty(group.getName())) {
			return false;
		}
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		if (db == null) {
			return false;
		}

		try {
			db.beginTransaction();
			db.execSQL("delete from " + DBOpenHelper.GROUP_TABLE_NAME
					+ " where _id = ?", new String[] { "" + group.getId() });
			group.setId(-1);
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		shouldUpdateGroupCache = true;
		return true;
	}

	/**
	 * Delete chess manual.
	 * 
	 * @param chessManual
	 *            the chess manual
	 * @return true, if successful
	 */
	public synchronized static boolean deleteHistoryChessManual(
			ChessManual chessManual) {
		if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
			return false;
		}
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		if (db == null) {
			return false;
		}

		try {
			db.beginTransaction();
			db.execSQL("delete from " + DBOpenHelper.HISTORY_TABLE_NAME
					+ " where sgfUrl = ?",
					new String[] { chessManual.getSgfUrl() });
			chessManual.setId(-1);
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		shouldUpdateHistoryCache = true;
		return true;
	}

	/**
	 * Delete chess manual.
	 * 
	 * @param chessManual
	 *            the chess manual
	 * @return true, if successful
	 */
	public synchronized static boolean deleteFavoriteChessManual(
			ChessManual chessManual) {
		if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
			return false;
		}
		SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
		if (db == null) {
			return false;
		}

		try {
			db.beginTransaction();
			db.execSQL("delete from " + DBOpenHelper.FAVORITE_TABLE_NAME
					+ " where sgfUrl = ?",
					new String[] { chessManual.getSgfUrl() });
			chessManual.setId(-1);
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		shouldUpdateGroupCache = true;
		shouldUpdateFavoriteCache = true;
		return true;
	}
}
