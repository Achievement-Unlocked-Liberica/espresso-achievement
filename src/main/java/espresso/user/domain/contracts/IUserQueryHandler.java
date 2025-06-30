package espresso.user.domain.contracts;

import espresso.common.domain.queries.QuerySizeType;
import espresso.common.domain.responses.HandlerResponse;
import espresso.user.domain.queries.GetUserByKeyQuery;

public interface IUserQueryHandler {   
     
    HandlerResponse<Object> handle(GetUserByKeyQuery qry);
    
    // HandlerResponse<Object> handle(CheckEmailExistsQuery qry);
    
    // HandlerResponse<Object> handle(CheckUsernameExistsQuery qry);

    Class<?> getDtoSize(QuerySizeType querySizeType);
}
