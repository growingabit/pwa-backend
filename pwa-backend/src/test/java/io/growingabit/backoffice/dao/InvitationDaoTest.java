package io.growingabit.backoffice.dao;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.SaveException;

import io.growingabit.backoffice.model.Invitation;
import io.growingabit.testUtils.BaseDatastoreTest;

public class InvitationDaoTest extends BaseDatastoreTest {

    private InvitationDao dao;

    @Before
    public void setUp() {
        ObjectifyService.register(Invitation.class);
        this.dao = new InvitationDao();
    }

    @Test
    public void persit() {
        Invitation invitation = new Invitation("My school", "My class", "This Year", "My Spec");
        dao.persist(invitation);
    }

    @Test(expected = SaveException.class)
    public void persitFail() {
        dao.persist(new Invitation());
    }

}
