package app.vku.vn.financemanager.dao;

import app.vku.vn.financemanager.model.Transaction;
import app.vku.vn.financemanager.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class TransactionDAO {
    public void save(Transaction transaction) {
        org.hibernate.Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(transaction);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    public void update(Transaction transaction) {
        org.hibernate.Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(transaction);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        org.hibernate.Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Transaction trans = session.get(Transaction.class, id);
            if (trans != null) {
                session.remove(trans);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    public Transaction getById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Transaction.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Transaction> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Transaction> query = session.createQuery("from Transaction", Transaction.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Transaction> getByCategory(Long categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Transaction> query = session.createQuery(
                "from Transaction t where t.category.id = :categoryId", 
                Transaction.class
            );
            query.setParameter("categoryId", categoryId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 