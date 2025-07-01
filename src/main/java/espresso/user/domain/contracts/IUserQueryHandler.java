package espresso.user.domain.contracts;

import espresso.common.domain.queries.QuerySizeType;
import espresso.common.domain.responses.HandlerResponse;
import espresso.user.domain.queries.GetUserByKeyQuery;
import espresso.user.domain.queries.GetUserNameExistsQuery;
import espresso.user.domain.queries.GetEmailExistsQuery;

public interface IUserQueryHandler {   
     
    HandlerResponse<Object> handle(GetUserByKeyQuery qry);
    
    HandlerResponse<Object> handle(GetUserNameExistsQuery qry);
    
    HandlerResponse<Object> handle(GetEmailExistsQuery qry);
    
    // HandlerResponse<Object> handle(CheckEmailExistsQuery qry);

    Class<?> getDtoSize(QuerySizeType querySizeType);
}
