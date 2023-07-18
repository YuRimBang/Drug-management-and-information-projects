package view;


import persistence.dto.AdminDTO;
import persistence.dto.InfirmaryDTO;
import persistence.dto.ManagerDTO;
import persistence.dto.UserDTO;

import java.util.List;

public class ManagerView
{
    public void printManager(List<ManagerDTO> dtos){
        System.out.println("관리자");
        for(ManagerDTO dto:dtos){
            System.out.println("dto.toString() = " + dto.toString());
        }
    }

    public void printAdmin(List<AdminDTO> dtos){
        System.out.println("운영자");
        for(AdminDTO dto:dtos){
            System.out.println("dto.toString() = " + dto.toString());
        }
    }

    public void printUser(List<UserDTO> dtos){
        System.out.println("회원");
        for(UserDTO dto:dtos){
            System.out.println("dto.toString() = " + dto.toString());
        }
    }

    public void printInfirmary(List<InfirmaryDTO> dtos){
        System.out.println("보건실");
        for(InfirmaryDTO dto:dtos){
            System.out.println("dto.toString() = " + dto.toString());
        }
    }
}
