package org.hablapps.meetup.logic

import play.api.Play.current
import play.api.db.slick.DB

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import scala.slick.driver.MySQLDriver.simple._
import org.hablapps.meetup.domain._
import org.hablapps.meetup.db._, MySQLInterpreter._

object SemiImpure {
  
  trait Store{
    def getGroup(gid: Int): Either[StoreError, Group]
    def getUser(uid: Int): Either[StoreError, User]
    def putJoin(join: JoinRequest): Either[StoreError, JoinRequest]
    def putMember(member: Member): Either[StoreError, Member]
    def isMember(uid: Int, gid: Int): Boolean
    def isPending(uid: Int, gid: Int): Boolean
  }

  object MySQLStore extends Store{

    def getGroup(gid: Int): Either[StoreError, Group] = 
      DB.withSession { implicit session =>
        val maybeGroup = (for { 
          group <- group_table if group.gid === gid
        } yield group).firstOption
        maybeGroup.fold[Either[StoreError,Group]](
          Left(NonExistentEntity(gid)))(
          Right(_))
      }
     
    def getUser(uid: Int): Either[StoreError, User] =  
      DB.withSession { implicit session =>
        val maybeUser = (for {
          user <- user_table if user.uid === uid
        } yield user).firstOption
        maybeUser.fold[Either[StoreError,User]](
          Left(NonExistentEntity(uid)))(
          Right(_))
      }
      
    def putJoin(join: JoinRequest): Either[StoreError, JoinRequest] = 
      DB.withSession { implicit session =>
        val maybeId = join_table returning join_table.map(_.jid) += join
        maybeId.fold[Either[StoreError, JoinRequest]](
          Left(GenericError(s"Error al insertar JoinRequest $join")))(
          _ => Right(join.copy(jid = maybeId)))
      }
  
    def putMember(member: Member): Either[StoreError, Member] = 
      DB.withSession { implicit session =>
        val maybeId = member_table returning member_table.map(_.mid) += member
        maybeId.fold[Either[StoreError, Member]](
          Left(GenericError(s"Error al insertar Member $member")))(
          _ => Right(member.copy(mid = maybeId)))
      }
  
    def isMember(uid: Int, gid: Int): Boolean = 
      DB.withSession { implicit session =>
        true
      }

    def isPending(uid: Int, gid: Int): Boolean = 
      DB.withSession { implicit session =>
        true
      }
  }

  trait Services{ self: Store => 
    
    def join(request: JoinRequest): Either[StoreError, JoinResponse] = {
      val JoinRequest(_, uid, gid) = request  
      for {
        user <- getUser(uid).right
        group <- getGroup(gid).right
        result <- 
          if (group.must_approve) 
            (putJoin(request).right map Left.apply).right
          else
            (putMember(Member(None, uid, gid)).right map Right.apply).right
      } yield result
    }

  }

}
 